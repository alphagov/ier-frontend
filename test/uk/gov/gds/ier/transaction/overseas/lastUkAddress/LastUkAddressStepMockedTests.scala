package uk.gov.gds.ier.transaction.overseas.lastUkAddress

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import org.specs2.mock.Mockito
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.service.AddressService
import org.mockito.Mockito._
import uk.gov.gds.ier.model.PartialAddress
import controllers.routes._
import controllers.step.overseas.LastUkAddressSelectController
import play.api.test.Helpers._
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.GoTo
import scala.Some
import play.api.test.FakeApplication

/**
 * Lover level tests focused on individual Step methods, like exit branching, utilizing
 * mocked services
 */
class LastUkAddressStepMockedTests extends FlatSpec with TestHelpers with Matchers with Mockito {

  val mockedJsonSerialiser = mock[JsonSerialiser]
  val mockedConfig = mock[Config]
  val mockedEncryptionService = mock[EncryptionService]
  val mockedAddressService = mock[AddressService]
  val scotPostcode = "EH1 1AA"
  val englPostcode = "WR2 6NJ"
  when (mockedAddressService.isScotland(scotPostcode)).thenReturn(true)
  when (mockedAddressService.isScotland(englPostcode)).thenReturn(false)
  val applicationWithScotLastUkAddress = InprogressOverseas(
    lastUkAddress = Some(PartialAddress(None, None, scotPostcode, None, None)))
  val applicationWithEnglLastUkAddress = InprogressOverseas(
    lastUkAddress = Some(PartialAddress(None, None, englPostcode, None, None)))
  // start Guice if it was not already started as one of the following tests does not work without it
  running(FakeApplication()) {}

  behavior of "LastUkAddressStep.nextStep"

  it should "redirect to Scotland exit page if address is Scottish (the gssCode starts with S)" in {
    val addressStep = new LastUkAddressStep(mockedJsonSerialiser, mockedConfig,
      mockedEncryptionService, mockedAddressService)
    val transferedState = addressStep.nextStep(applicationWithScotLastUkAddress)
    transferedState should be (GoTo(ExitController.scotland))
  }

  it should "redirect to next address step if address is English" in {
    val addressStep = new LastUkAddressStep(mockedJsonSerialiser, mockedConfig,
      mockedEncryptionService, mockedAddressService)
    val transferedState = addressStep.nextStep(applicationWithEnglLastUkAddress)
    transferedState should be (LastUkAddressSelectController.lastUkAddressSelectStep)
  }
}
