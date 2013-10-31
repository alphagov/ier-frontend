package uk.gov.gds.ier.client

import uk.gov.gds.ier.model.{Fail, ApiResponse, Success}
import play.api.libs.ws.WS
import scala.concurrent.duration._
import scala.concurrent.Await
import play.api.http._
import uk.gov.gds.ier.config.Config
import com.google.inject.Inject
import uk.gov.gds.ier.guice.WithConfig

trait ApiClient {

  self:WithConfig =>

    def get(url: String) : ApiResponse = {
      val result = Await.result(
        WS.url(url).get(),
        config.apiTimeout seconds
      )
      result.status match {
        case Status.OK => {
          Success(result.body)
        }
        case _ => {
          Fail(result.body)
        }
      }
    }

    def post(url:String, content:String, headers: (String, String)*) : ApiResponse = {
      val result = Await.result(
        WS.url(url)
          .withHeaders("Content-Type" -> MimeTypes.JSON)
          .withHeaders(headers:_*)
          .post(content),
        config.apiTimeout seconds
      )
      result.status match {
        case Status.OK => Success(result.body)
        case Status.NO_CONTENT => Success("")
        case _ => Fail(result.body)
      }
    }
}

class IerApiClient @Inject() (configuration: Config) extends ApiClient with WithConfig {
  val config = configuration
}

class PlacesApiClient @Inject() (configuration: Config) extends ApiClient with WithConfig {
  val config = configuration
}


