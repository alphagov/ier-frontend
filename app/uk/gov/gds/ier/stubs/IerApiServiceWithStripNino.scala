package uk.gov.gds.ier.stubs

import com.google.inject.Inject
import uk.gov.gds.ier.service.{ConcreteIerApiService, IerApiService}
import uk.gov.gds.ier.model.{InprogressOrdinary, InprogressOverseas, Nino, InprogressApplication}
import scala.util.Random

class IerApiServiceWithStripNino @Inject() (ierService: ConcreteIerApiService) extends IerApiService {

  override def submitOrdinaryApplication(ipAddress: Option[String],
                                         applicant: InprogressOrdinary,
                                         referenceNumber: Option[String]) = {
    applicant.nino match {
      case Some(Nino(None, Some(noNinoReason))) => ierService.submitOrdinaryApplication(ipAddress, applicant, referenceNumber)
      case Some(Nino(Some(nino), None)) => ierService.submitOrdinaryApplication(
          ipAddress,
          applicant.copy(nino = Some(Nino(Some(randomNino()), None))),
          referenceNumber)
      case unexpectedNino => throw new IllegalArgumentException("Unexpected NINO: " + unexpectedNino)
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
              ordinary.copy(nino = Some(Nino(Some(randomNino()), None)))
            )
          }
          case unexpectedNino => throw new IllegalArgumentException("Unexpected NINO: " + unexpectedNino)
        }
      case overseas:InprogressOverseas => {
        ierService.generateReferenceNumber[InprogressOverseas](overseas)
      }
    }
  }

  private[stubs] def randomNino() = {
    def num = {
      Random.nextInt(9)
    }
    s"XX $num$num $num$num $num$num A"
  }
}
