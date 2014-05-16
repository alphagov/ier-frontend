package uk.gov.gds.ier.transaction.forces.address

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.specs2.mock.Mockito
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.GoTo
import controllers.routes.ExitController
import org.mockito.Mockito._
import uk.gov.gds.ier.step.Step
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.assets.RemoteAssets

/*
 * This test mock the AddressService.
 *
 * So it is separated from the normal AddressStepTests
 */
class AddressStepMockedTests extends FlatSpec with TestHelpers with Matchers with Mockito {

  it should "redirect to Scotland exit page if the gssCode starts with S" in {
    val mockedJsonSerialiser = mock[JsonSerialiser]
    val mockedConfig = mock[Config]
    val mockedEncryptionService = mock[EncryptionService]
    val mockedAddressService = mock[AddressService]
    val mockedRemoteAssets = mock[RemoteAssets]

    val addressStep = new AddressStep(
      mockedJsonSerialiser,
      mockedConfig,
      mockedEncryptionService,
      mockedAddressService,
      mockedRemoteAssets
    )

    val postcode = "EH1 1AA"

    when (mockedAddressService.isScotland(postcode)).thenReturn(true)
    val currentState = completeForcesApplication.copy(
    		address = Some(LastUkAddress(Some(true),
    		    Some(PartialAddress(None, None, postcode, None, None)))))

    val transferedState = addressStep.nextStep(currentState)
    transferedState should be (GoTo(ExitController.scotland))
  }
}
