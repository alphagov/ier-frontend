package uk.gov.gds.ier.transaction.overseas.parentsAddress

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.model.PartialAddress
import uk.gov.gds.ier.controller.routes._
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.GoTo
import uk.gov.gds.ier.assets.RemoteAssets

class ParentsAddressStepMockedTest
  extends MockingControllerTestSuite
  with WithMockOverseasControllers {

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

  behavior of "ParentsAddressStep.nextStep"

  it should "redirect to Scotland exit page if address is Scottish (the gssCode starts with S)" in {
    val addressStep = new ParentsAddressStep(
      mockedJsonSerialiser,
      mockedConfig,
      mockedEncryptionService,
      mockedAddressService,
      mockedRemoteAssets,
      overseas
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
      mockedRemoteAssets,
      overseas
    )
    val transferedState = addressStep.nextStep(applicationWithEnglParentsAddress)
    transferedState should be (mockParentsAddressSelectStep)
  }
}
