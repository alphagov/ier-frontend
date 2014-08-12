package uk.gov.gds.ier.transaction.overseas.parentsAddress

import uk.gov.gds.ier.test.MockingControllerTestSuite
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.model.PartialAddress
import controllers.routes._
import controllers.step.overseas.ParentsAddressSelectController
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.GoTo
import uk.gov.gds.ier.assets.RemoteAssets

class ParentsAddressStepMockedTest extends MockingControllerTestSuite {

  val mockedJsonSerialiser = mock[JsonSerialiser]
  val mockedConfig = mock[Config]
  val mockedEncryptionService = mock[EncryptionService]
  val mockedAddressService = mock[AddressService]
  val mockedRemoteAssets = mock[RemoteAssets]
  val scotPostcode = "EH1 1AA"
  val englPostcode = "WR2 6NJ"
  when (mockedAddressService.isScotland(scotPostcode)).thenReturn(true)
  when (mockedAddressService.isScotland(englPostcode)).thenReturn(false)
  val applicationWithScotParentsAddress = InprogressOverseas(
    parentsAddress = Some(PartialAddress(None, None, scotPostcode, None, None)))
  val applicationWithEnglParentsAddress = InprogressOverseas(
    parentsAddress = Some(PartialAddress(None, None, englPostcode, None, None)))
  // start Guice if it was not already started as one of the following tests does not work without it
  running(FakeApplication()) {}

  behavior of "ParentsAddressStep.nextStep"

  it should "redirect to Scotland exit page if address is Scottish (the gssCode starts with S)" in {
    val addressStep = new ParentsAddressStep(
      mockedJsonSerialiser,
      mockedConfig,
      mockedEncryptionService,
      mockedAddressService,
      mockedRemoteAssets
    )
    val transferedState = addressStep.nextStep(applicationWithScotParentsAddress)
    transferedState should be (GoTo(ExitController.scotland))
  }

  it should "redirect to next address step if address is English" in {
    val addressStep = new ParentsAddressStep(
      mockedJsonSerialiser,
      mockedConfig,
      mockedEncryptionService,
      mockedAddressService,
      mockedRemoteAssets
    )
    val transferedState = addressStep.nextStep(applicationWithEnglParentsAddress)
    transferedState should be (ParentsAddressSelectController.parentsAddressSelectStep)
  }
}
