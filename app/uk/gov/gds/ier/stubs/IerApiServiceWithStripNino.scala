package uk.gov.gds.ier.stubs

import com.google.inject.Inject
import uk.gov.gds.ier.client.IerApiClient
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.service.{ConcreteIerApiService, IerApiService, PlacesService}
import uk.gov.gds.ier.digest.ShaHashProvider
import uk.gov.gds.ier.model.{Nino, ApiApplicationResponse, InprogressApplication}

class IerApiServiceWithStripNino @Inject() (ierService: ConcreteIerApiService) extends IerApiService {

  override def submitApplication(ipAddress: Option[String], applicant: InprogressApplication, referenceNumber: Option[String]): ApiApplicationResponse = {
    ierService.submitApplication(ipAddress, applicant.copy(nino = Some(Nino(Some("AB 12 34 56 D"), None))), referenceNumber)
  }

  def generateReferenceNumber(application: InprogressApplication): String = {
    ierService.generateReferenceNumber(application.copy(nino = Some(Nino(Some("AB 12 34 56 D"), None))))
  }
}
