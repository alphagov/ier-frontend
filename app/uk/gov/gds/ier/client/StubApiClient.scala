package uk.gov.gds.ier.client

import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.{ApiApplicationResponse, Success, ApiResponse}
import java.util.UUID
import org.joda.time.DateTime
import uk.gov.gds.ier.serialiser.JsonSerialiser

@Singleton
class StubApiClient @Inject() (config: Config, serialiser: JsonSerialiser) extends ApiClient(config) {
  override def post(url:String, content:String,headers: (String, String)*): ApiResponse = {
    if (url.contains("/citizen/application")) {
      println("Stubbing POST to " + url)
      Success(serialiser.toJson(ApiApplicationResponse(UUID.randomUUID().toString, DateTime.now().toString, "success", "web", "fake-gsscode")))
    } else {
      super.get(url)
    }
  }
}
