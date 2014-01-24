package uk.gov.gds.ier.stubs

import org.scalatest.{FlatSpec, Matchers}
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.service.ConcreteIerApiService
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import uk.gov.gds.ier.model.InprogressOrdinary
import scala.Some
import uk.gov.gds.ier.model.Nino

class IerApiServiceWithStipNinoTests extends FlatSpec with Matchers with MockitoSugar {
  
  val stripNinoGeneratorMock = mock[StripNinoGenerator]
  when(stripNinoGeneratorMock.generate).thenReturn("XX 90 87 46 B") 

  it should "replace a nino when submitting Application" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService] 
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock, stripNinoGeneratorMock)
    val applicationWithNino = InprogressOrdinary(nino = Some(Nino(Some("12345"), None)))
    val applicationWithStrippedNino = InprogressOrdinary(nino = Some(Nino(Some("XX 90 87 46 B"), None)))

    when(concreteIerApiServiceMock.submitOrdinaryApplication(None, applicationWithStrippedNino, None)).thenReturn(ApiApplicationResponse("","","","","")) //don't care about return type
    service.submitOrdinaryApplication(None, applicationWithNino, None)
    verify(concreteIerApiServiceMock).submitOrdinaryApplication(None, applicationWithStrippedNino, None)
  }

  it should "replace a nino when generating Reference Number" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock, stripNinoGeneratorMock)
    val applicationWithNino = InprogressOrdinary(nino = Some(Nino(Some("12345"), None)))
    val applicationWithStrippedNino = InprogressOrdinary(nino = Some(Nino(Some("XX 90 87 46 B"), None)))

    when(concreteIerApiServiceMock.generateReferenceNumber(applicationWithStrippedNino)).thenReturn("a1b2c3d4") //don't care about return type
    service.generateReferenceNumber(applicationWithNino)
    verify(concreteIerApiServiceMock).generateReferenceNumber(applicationWithStrippedNino)
  }

  it should "not replace a nino when using no nino reason" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNoNinoReason = InprogressOrdinary(nino = Some(Nino(None, Some("no nino reason"))))

    when(concreteIerApiServiceMock.submitOrdinaryApplication(None, applicationWithNoNinoReason, None)).thenReturn(ApiApplicationResponse("","","","","")) //don't care about return type
    service.submitOrdinaryApplication(None, applicationWithNoNinoReason, None)
    verify(concreteIerApiServiceMock).submitOrdinaryApplication(None, applicationWithNoNinoReason, None)
  }
  
  it should "strip nino generator should always generate random number but starting with XX and validating against pattern" in {
    StripNinoGenerator.generate() should not be StripNinoGenerator.generate() should not be StripNinoGenerator.generate()
    StripNinoGenerator.generate() should startWith("XX")
    StripNinoGenerator.generate() should fullyMatch regex """XX \d{2} \d{2} \d{2} [A-E]{1}"""
  }
}
