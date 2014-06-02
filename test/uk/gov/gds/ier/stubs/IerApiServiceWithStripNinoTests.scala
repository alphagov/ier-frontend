package uk.gov.gds.ier.stubs

import org.scalatest.{FlatSpec, Matchers}
import uk.gov.gds.ier.model._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.AdditionalMatchers
import org.mockito.{Matchers => MockitoMatchers}
import scala.Some
import uk.gov.gds.ier.model.Nino
import uk.gov.gds.ier.service.apiservice.{IerApiApplicationResponse, ConcreteIerApiService}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class IerApiServiceWithStripNinoTests extends FlatSpec with Matchers with MockitoSugar {

  def isNot[T](obj:T) = AdditionalMatchers.not(MockitoMatchers.eq(obj))

  behavior of "IerApiServiceWithStripNino"

  it should "replace a nino when submitting Application (ordinary) when calling API service" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNino = InprogressOrdinary(nino = Some(Nino(Some("12345"), None)))

    service.submitOrdinaryApplication(None, applicationWithNino, None, Some("1234"))

    verify(concreteIerApiServiceMock).submitOrdinaryApplication(
      MockitoMatchers.eq(None),
      isNot(applicationWithNino),
      MockitoMatchers.eq(None),
      MockitoMatchers.eq(Some("1234"))
    )
  }

  it should "replace a nino when submitting Application (overseas) when calling API service" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNino = InprogressOverseas(nino = Some(Nino(Some("12345"), None)))

    service.submitOverseasApplication(None, applicationWithNino, None, Some("1234"))

    verify(concreteIerApiServiceMock).submitOverseasApplication(
      MockitoMatchers.eq(None),
      isNot(applicationWithNino),
      MockitoMatchers.eq(None),
      MockitoMatchers.eq(Some("1234"))
    )
  }

  it should "replace a nino when submitting Application (forces) when calling API service" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNino = InprogressForces(nino = Some(Nino(Some("12345"), None)))

    service.submitForcesApplication(None, applicationWithNino, None, Some("1234"))

    verify(concreteIerApiServiceMock).submitForcesApplication(
      MockitoMatchers.eq(None),
      isNot(applicationWithNino),
      MockitoMatchers.eq(None),
      MockitoMatchers.eq(Some("1234"))
    )
  }

  it should "replace a nino when submitting Application (crown) when calling API service" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNino = InprogressCrown(nino = Some(Nino(Some("12345"), None)))

    service.submitCrownApplication(None, applicationWithNino, None, Some("1234"))

    verify(concreteIerApiServiceMock).submitCrownApplication(
      MockitoMatchers.eq(None),
      isNot(applicationWithNino),
      MockitoMatchers.eq(None),
      MockitoMatchers.eq(Some("1234"))
    )
  }

  it should "replace a nino when generating Reference Number (ordinary)" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNino = InprogressOrdinary(nino = Some(Nino(Some("12345"), None)))

    service.generateOrdinaryReferenceNumber(applicationWithNino)

    verify(concreteIerApiServiceMock).generateOrdinaryReferenceNumber(
      isNot(applicationWithNino))
  }

  it should "not replace a nino when using no nino reason (ordinary)" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNoNinoReason = InprogressOrdinary(
      nino = Some(Nino(None, Some("no nino reason"))))

    service.submitOrdinaryApplication(None, applicationWithNoNinoReason, None, Some("1234"))

    verify(concreteIerApiServiceMock).submitOrdinaryApplication(
      None,
      applicationWithNoNinoReason,
      None,
      Some("1234")
    )
  }

  it should "replace a nino when generating Reference Number (overseas)" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNino = InprogressOverseas(nino = Some(Nino(Some("12345"), None)))

    service.generateOverseasReferenceNumber(applicationWithNino)

    verify(concreteIerApiServiceMock).generateOverseasReferenceNumber(
      isNot(applicationWithNino))
  }

  it should "not replace a nino when using no nino reason (overseas)" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNoNinoReason = InprogressOverseas(
      nino = Some(Nino(None, Some("no nino reason"))))

    service.submitOverseasApplication(None, applicationWithNoNinoReason, None, Some("1234"))

    verify(concreteIerApiServiceMock).submitOverseasApplication(
      None,
      applicationWithNoNinoReason,
      None,
      Some("1234")
    )
  }

  it should "replace a nino when generating Reference Number (forces)" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNino = InprogressForces(nino = Some(Nino(Some("12345"), None)))

    service.generateForcesReferenceNumber(applicationWithNino)

    verify(concreteIerApiServiceMock).generateForcesReferenceNumber(
      isNot(applicationWithNino))
  }

  it should "not replace a nino when using no nino reason (forces)" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNoNinoReason = InprogressForces(
      nino = Some(Nino(None, Some("no nino reason"))))

    service.submitForcesApplication(None, applicationWithNoNinoReason, None, Some("1234"))

    verify(concreteIerApiServiceMock).submitForcesApplication(
      None,
      applicationWithNoNinoReason,
      None,
      Some("1234")
    )
  }


  it should "replace a nino when generating Reference Number (crown)" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNino = InprogressCrown(nino = Some(Nino(Some("12345"), None)))

    service.generateCrownReferenceNumber(applicationWithNino)

    verify(concreteIerApiServiceMock).generateCrownReferenceNumber(
      isNot(applicationWithNino))
  }

  it should "not replace a nino when using no nino reason (crown)" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNoNinoReason = InprogressCrown(
      nino = Some(Nino(None, Some("no nino reason"))))

    service.submitCrownApplication(None, applicationWithNoNinoReason, None, Some("1234"))

    verify(concreteIerApiServiceMock).submitCrownApplication(
      None,
      applicationWithNoNinoReason,
      None,
      Some("1234")
    )
  }

  it should "strip nino generator should always generate random number but starting with XX and validating against pattern" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    service.randomNino() should not be service.randomNino() should not be service.randomNino()
    service.randomNino() should startWith("XX")
    service.randomNino() should fullyMatch regex """XX \d{2} \d{2} \d{2} A"""
  }
}
