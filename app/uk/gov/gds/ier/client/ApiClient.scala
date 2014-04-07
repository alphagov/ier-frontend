package uk.gov.gds.ier.client

import uk.gov.gds.ier.model.{Fail, ApiResponse, Success}
import play.api.libs.ws.WS
import scala.concurrent.duration._
import scala.concurrent.Await
import play.api.http._
import uk.gov.gds.ier.guice.WithConfig
import org.joda.time.DateTime
import uk.gov.gds.ier.logging.Logging

trait ApiClient extends Logging {
  self:WithConfig =>

  def get(url: String) : ApiResponse = {

    val start = new DateTime()
    try {
        val result = Await.result(
          WS.url(url).get(),
          config.apiTimeout seconds
        )
        result.status match {
          case Status.OK => {
            val timeTakenMs = DateTime.now.minus(start.getMillis).getMillis
            logger.info(s"apiClient.get url:$url result:200 timeTakenMs:$timeTakenMs")
            Success(result.body, timeTakenMs)
          }
          case status => {
            val timeTakenMs = DateTime.now.minus(start.getMillis).getMillis
            logger.info(
              s"apiClient.get url:$url result:$status timeTakenMs:$timeTakenMs reason:${result.body}")
            Fail(result.body, timeTakenMs)
          }
        }
    } catch {
      case e:Exception => {
        val timeTakenMs = DateTime.now.minus(start.getMillis).getMillis
        logger.error(
          s"apiClient.get url:$url timeTakenMs:$timeTakenMs exception:${e.getStackTraceString}")
        Fail(e.getMessage, timeTakenMs)
      }
    }
  }

  def post(
      url:String,
      content:String,
      headers: (String, String)*) : ApiResponse = {

    val start = new DateTime()
    try {
      val result = Await.result(
        WS.url(url)
          .withHeaders("Content-Type" -> MimeTypes.JSON)
          .withHeaders(headers:_*)
          .post(content),
        config.apiTimeout seconds
      )
      result.status match {
        case Status.OK => {
          val timeTakenMs = DateTime.now.minus(start.getMillis).getMillis
          logger.info(s"apiClient.post url:$url result:200 timeTakenMs:$timeTakenMs")
          Success(result.body, timeTakenMs)
        }
        case Status.NO_CONTENT => {
          val timeTakenMs = DateTime.now.minus(start.getMillis).getMillis
          logger.info(s"apiClient.post url:$url result:204 timeTakenMs:$timeTakenMs")
          Success("", timeTakenMs)
        }
        case status => {
          val timeTakenMs = DateTime.now.minus(start.getMillis).getMillis
          logger.info(s"apiClient.post url:$url result:$status timeTakenMs:$timeTakenMs")
          Fail(result.body, timeTakenMs)
        }
      }
    } catch {
      case e:Exception => {
        val timeTakenMs = DateTime.now.minus(start.getMillis).getMillis
        logger.error(
          s"apiClient.post url:$url timeTakenMs:$timeTakenMs exception:${e.getStackTraceString}")
        Fail(e.getMessage, timeTakenMs)
      }
    }
  }
}
