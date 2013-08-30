package uk.gov.gds.ier.client

import uk.gov.gds.ier.model.{Fail, ApiResponse, Success}
import play.api.libs.ws.WS
import scala.concurrent.duration._
import scala.concurrent.Await
import play.api.http._
import uk.gov.gds.ier.config.Config

class ApiClient {

  def get(url: String, headers: (String, String)*) : ApiResponse = {
    val result = Await.result(
      WS.url(url)
        .withHeaders(headers + "Content-Type" -> MimeTypes.JSON).get(),
      Config.apiTimeout seconds
    )
    result.status match {
      case Status.OK => Success(result.body)
      case _ => Fail(result.body)
    }
  }

  def post(url:String, content:String) : ApiResponse = {
    val result = Await.result(
      WS.url(url)
        .withHeaders("Content-Type" -> MimeTypes.JSON)
        .post(content),
      Config.apiTimeout seconds
    )
    result.status match {
      case Status.OK => Success(result.body)
      case _ => Fail(result.body)
    }
  }
}
