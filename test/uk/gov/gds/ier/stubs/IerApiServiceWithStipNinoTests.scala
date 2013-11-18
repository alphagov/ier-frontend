package uk.gov.gds.ier.stubs

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.client.{PlacesApiClient, IerApiClient}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.service.{ConcreteIerApiService, PlacesService}
import uk.gov.gds.common.model.LocalAuthority
import uk.gov.gds.ier.digest.ShaHashProvider
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import uk.gov.gds.ier.model.InprogressApplication
import scala.Some
import uk.gov.gds.ier.model.Nino

class IerApiServiceWithStipNinoTests extends FlatSpec with Matchers with MockitoSugar{

  it should "replace a nino when submitting Application" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNino = InprogressApplication(nino = Some(Nino(Some("12345"), None)))
    val applicationWithStrippedNino = InprogressApplication(nino = Some(Nino(Some("AB 12 34 56 D"), None)))

    when(concreteIerApiServiceMock.submitApplication(None, applicationWithStrippedNino, None)).thenReturn(ApiApplicationResponse("","","","","")) //don't care about return type
    service.submitApplication(None, applicationWithNino, None)
    verify(concreteIerApiServiceMock).submitApplication(None, applicationWithStrippedNino, None)
  }

  it should "replace a nino when generating Reference Number" in {
    val concreteIerApiServiceMock = mock[ConcreteIerApiService]
    val service = new IerApiServiceWithStripNino(concreteIerApiServiceMock)
    val applicationWithNino = InprogressApplication(nino = Some(Nino(Some("12345"), None)))
    val applicationWithStrippedNino = InprogressApplication(nino = Some(Nino(Some("AB 12 34 56 D"), None)))

    when(concreteIerApiServiceMock.generateReferenceNumber(applicationWithStrippedNino)).thenReturn("a1b2c3d4") //don't care about return type
    service.generateReferenceNumber(applicationWithNino)
    verify(concreteIerApiServiceMock).generateReferenceNumber(applicationWithStrippedNino)
  }
}
