package uk.gov.gds.ier.stubs

import com.google.inject.Inject
import uk.gov.gds.ier.client.IerApiClient
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.service.{ConcreteIerApiService, IerApiService, PlacesService}
import uk.gov.gds.ier.digest.ShaHashProvider
import uk.gov.gds.ier.model.{InprogressOrdinary, InprogressOverseas, Nino, ApiApplicationResponse, InprogressApplication}

class IerApiServiceWithStripNino @Inject() (ierService: ConcreteIerApiService, stripNinoGenerator: StripNinoGenerator = StripNinoGenerator) extends IerApiService {

  override def submitOrdinaryApplication(ipAddress: Option[String],
                                         applicant: InprogressOrdinary,
                                         referenceNumber: Option[String]) = {
    applicant.nino match {
      case Some(Nino(None, Some(noNinoReason))) => ierService.submitOrdinaryApplication(ipAddress, applicant, referenceNumber)
      case Some(Nino(Some(nino), None)) => ierService.submitOrdinaryApplication(
          ipAddress, 
          applicant.copy(nino = Some(Nino(Some(stripNinoGenerator.generate), None))), 
          referenceNumber)
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
              ordinary.copy(nino = Some(Nino(Some(stripNinoGenerator.generate), None)))
            )
          }
        }
      case overseas:InprogressOverseas => {
        ierService.generateReferenceNumber[InprogressOverseas](overseas)
      }
    }
  }
}

trait StripNinoGenerator {
  def generate(): String
}

/**
 * Generate stub NINO for testing, current rules: starts with 'XX' followed by 6 random digits and letter from 'A' .. 'E'  
 * Example: XX 90 87 46 B
 */
object StripNinoGenerator extends StripNinoGenerator {
  def generate() = {
    import scala.util.Random._
    "XX %02d %02d %02d %s".format(nextInt(99), nextInt(99), nextInt(99), ('A'.toByte + nextInt(5)).toChar)
  }
}