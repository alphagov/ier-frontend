package uk.gov.gds.ier.stubs

import org.scalatest.{FlatSpec, Matchers}
import uk.gov.gds.ier.model._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.AdditionalMatchers
import org.mockito.{Matchers => MockitoMatchers}
import uk.gov.gds.ier.model.InprogressOrdinary
import scala.Some
import uk.gov.gds.ier.model.Nino
import uk.gov.gds.ier.service.apiservice.{IerApiApplicationResponse, ConcreteIerApiService}

class IerApiServiceWithStipNinoTests extends FlatSpec with Matchers with MockitoSugar {

  def isNot[T](obj:T) = AdditionalMatchers.not(MockitoMatchers.eq(obj))

  it should "replace a nino when submitting Application (ordinary)" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNino = InprogressOrdinary(nino = Some(Nino(Some("12345"), None)))

    when(
      concreteIerApiServiceMock.submitOrdinaryApplication(
        MockitoMatchers.eq(None),
        isNot(applicationWithNino),
        MockitoMatchers.eq(None)
      )
    ).thenReturn(IerApiApplicationResponse("","","","","")) //don't care about return type
    service.submitOrdinaryApplication(None, applicationWithNino, None)
    verify(concreteIerApiServiceMock).submitOrdinaryApplication(
      MockitoMatchers.eq(None),
      isNot(applicationWithNino),
      MockitoMatchers.eq(None)
    )
  }

  it should "replace a nino when submitting Application (overseas)" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNino = InprogressOverseas(nino = Some(Nino(Some("12345"), None)))

    when(
      concreteIerApiServiceMock.submitOverseasApplication(
        MockitoMatchers.eq(None),
        isNot(applicationWithNino),
        MockitoMatchers.eq(None)
      )
    ).thenReturn(IerApiApplicationResponse("","","","","")) //don't care about return type
    service.submitOverseasApplication(None, applicationWithNino, None)
    verify(concreteIerApiServiceMock).submitOverseasApplication(
      MockitoMatchers.eq(None),
      isNot(applicationWithNino),
      MockitoMatchers.eq(None)
    )
  }

  it should "replace a nino when submitting Application (forces)" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNino = InprogressForces(nino = Some(Nino(Some("12345"), None)))

    when(
      concreteIerApiServiceMock.submitForcesApplication(
        MockitoMatchers.eq(None),
        isNot(applicationWithNino),
        MockitoMatchers.eq(None)
      )
    ).thenReturn(IerApiApplicationResponse("","","","","")) //don't care about return type
    service.submitForcesApplication(None, applicationWithNino, None)
    verify(concreteIerApiServiceMock).submitForcesApplication(
      MockitoMatchers.eq(None),
      isNot(applicationWithNino),
      MockitoMatchers.eq(None)
    )
  }

  it should "replace a nino when submitting Application (crown)" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNino = InprogressCrown(nino = Some(Nino(Some("12345"), None)))

    when(
      concreteIerApiServiceMock.submitCrownApplication(
        MockitoMatchers.eq(None),
        isNot(applicationWithNino),
        MockitoMatchers.eq(None)
      )
    ).thenReturn(IerApiApplicationResponse("","","","","")) //don't care about return type
    service.submitCrownApplication(None, applicationWithNino, None)
    verify(concreteIerApiServiceMock).submitCrownApplication(
      MockitoMatchers.eq(None),
      isNot(applicationWithNino),
      MockitoMatchers.eq(None)
    )
  }

  it should "replace a nino when generating Reference Number (ordinary)" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNino = InprogressOrdinary(nino = Some(Nino(Some("12345"), None)))

    when(concreteIerApiServiceMock.generateOrdinaryReferenceNumber(
      isNot(applicationWithNino))).thenReturn("a1b2c3d4") //don't care about return type
    service.generateOrdinaryReferenceNumber(applicationWithNino)
    verify(concreteIerApiServiceMock).generateOrdinaryReferenceNumber(
      isNot(applicationWithNino))
  }

  it should "not replace a nino when using no nino reason (ordinary)" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNoNinoReason = InprogressOrdinary(
      nino = Some(Nino(None, Some("no nino reason"))))

    when(concreteIerApiServiceMock.submitOrdinaryApplication(
      None, applicationWithNoNinoReason, None))
      .thenReturn(IerApiApplicationResponse("","","","","")) //don't care about return type
    service.submitOrdinaryApplication(None, applicationWithNoNinoReason, None)
    verify(concreteIerApiServiceMock).submitOrdinaryApplication(
      None, applicationWithNoNinoReason, None)
  }

  it should "replace a nino when generating Reference Number (overseas)" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNino = InprogressOverseas(nino = Some(Nino(Some("12345"), None)))

    when(concreteIerApiServiceMock.generateOverseasReferenceNumber(
      isNot(applicationWithNino))).thenReturn("a1b2c3d4") //don't care about return type
    service.generateOverseasReferenceNumber(applicationWithNino)
    verify(concreteIerApiServiceMock).generateOverseasReferenceNumber(
      isNot(applicationWithNino))
  }

  it should "not replace a nino when using no nino reason (overseas)" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNoNinoReason = InprogressOverseas(
      nino = Some(Nino(None, Some("no nino reason"))))

    when(concreteIerApiServiceMock.submitOverseasApplication(
      None, applicationWithNoNinoReason, None))
      .thenReturn(IerApiApplicationResponse("","","","","")) //don't care about return type
    service.submitOverseasApplication(None, applicationWithNoNinoReason, None)
    verify(concreteIerApiServiceMock).submitOverseasApplication(
      None, applicationWithNoNinoReason, None)
  }

  it should "replace a nino when generating Reference Number (forces)" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNino = InprogressForces(nino = Some(Nino(Some("12345"), None)))

    when(concreteIerApiServiceMock.generateForcesReferenceNumber(
      isNot(applicationWithNino))).thenReturn("a1b2c3d4") //don't care about return type
    service.generateForcesReferenceNumber(applicationWithNino)
    verify(concreteIerApiServiceMock).generateForcesReferenceNumber(
      isNot(applicationWithNino))
  }

  it should "not replace a nino when using no nino reason (forces)" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNoNinoReason = InprogressForces(
      nino = Some(Nino(None, Some("no nino reason"))))

    when(concreteIerApiServiceMock.submitForcesApplication(
      None, applicationWithNoNinoReason, None))
      .thenReturn(IerApiApplicationResponse("","","","","")) //don't care about return type
    service.submitForcesApplication(None, applicationWithNoNinoReason, None)
    verify(concreteIerApiServiceMock).submitForcesApplication(
      None, applicationWithNoNinoReason, None)
  }


  it should "replace a nino when generating Reference Number (crown)" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNino = InprogressCrown(nino = Some(Nino(Some("12345"), None)))

    when(concreteIerApiServiceMock.generateCrownReferenceNumber(
      isNot(applicationWithNino))).thenReturn("a1b2c3d4") //don't care about return type
    service.generateCrownReferenceNumber(applicationWithNino)
    verify(concreteIerApiServiceMock).generateCrownReferenceNumber(
      isNot(applicationWithNino))
  }

  it should "not replace a nino when using no nino reason (crown)" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNoNinoReason = InprogressCrown(
      nino = Some(Nino(None, Some("no nino reason"))))

    when(concreteIerApiServiceMock.submitCrownApplication(
      None, applicationWithNoNinoReason, None))
      .thenReturn(IerApiApplicationResponse("","","","","")) //don't care about return type
    service.submitCrownApplication(None, applicationWithNoNinoReason, None)
    verify(concreteIerApiServiceMock).submitCrownApplication(
      None, applicationWithNoNinoReason, None)
  }

  it should "strip nino generator should always generate random number but starting with XX and validating against pattern" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    service.randomNino() should not be service.randomNino() should not be service.randomNino()
    service.randomNino() should startWith("XX")
    service.randomNino() should fullyMatch regex """XX \d{2} \d{2} \d{2} A"""
  }
}
