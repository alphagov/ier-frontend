package uk.gov.gds.ier.stubs

import com.google.inject.Inject
import uk.gov.gds.ier.client.IerApiClient
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.service.{ConcreteIerApiService, IerApiService, PlacesService}
import uk.gov.gds.ier.digest.ShaHashProvider
import uk.gov.gds.ier.model.{InprogressOrdinary, InprogressOverseas, Nino, ApiApplicationResponse, InprogressApplication}

class IerApiServiceWithStripNino @Inject() (ierService: ConcreteIerApiService) extends IerApiService {

  override def submitOrdinaryApplication(ipAddress: Option[String],
                                         applicant: InprogressOrdinary,
                                         referenceNumber: Option[String]) = {
    applicant.nino match {
      case Some(Nino(None, Some(noNinoReason))) => ierService.submitOrdinaryApplication(ipAddress, applicant, referenceNumber)
      case Some(Nino(Some(nino), None)) => ierService.submitOrdinaryApplication(ipAddress, applicant.copy(nino = Some(Nino(Some("AB 12 34 56 D"), None))), referenceNumber)
    }
  }

  override def submitOverseasApplication(ipAddress: Option[String],
                                         applicant: InprogressOverseas,
                                         referenceNumber: Option[String]) = {
    ierService.submitOverseasApplication(ipAddress, applicant, referenceNumber)
  }

  override def generateReferenceNumber[T <: InprogressApplication[T]](application: T) = {
    application match {
      case ordinary:InprogressOrdinary =>
        ordinary.nino match {
          case Some(Nino(None, Some(noNinoReason))) => {
            ierService.generateReferenceNumber[InprogressOrdinary](ordinary)
          }
          case Some(Nino(Some(nino), None)) => {
            ierService.generateReferenceNumber[InprogressOrdinary](
                ordinary.copy(nino = Some(Nino(Some("AB 12 34 56 D"), None)))
            )
          }
        }
      case overseas:InprogressOverseas => {
        ierService.generateReferenceNumber[InprogressOverseas](overseas)
      }
    }
  }
}
