package uk.gov.gds.ier.stubs

import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.{Fail, Success, ApiResponse}
import java.util.UUID
import org.joda.time.DateTime
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.client.IerApiClient
import uk.gov.gds.ier.service.apiservice.IerApiApplicationResponse

@Singleton
class IerStubApiClient @Inject() (config: Config, serialiser: JsonSerialiser) extends IerApiClient(config) {
  override def post(url:String, content:String,headers: (String, String)*): ApiResponse = {
    if (url.contains("/citizen/application")) {
      Success(serialiser.toJson(IerApiApplicationResponse(UUID.randomUUID().toString, DateTime.now().toString, "success", "web", "fake-gsscode")))
    } else {
      super.get(url)
    }
  }
}
