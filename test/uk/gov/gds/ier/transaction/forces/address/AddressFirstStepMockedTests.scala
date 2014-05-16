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
class AddressFirstStepMockedTests extends FlatSpec with TestHelpers with Matchers with Mockito {

  it should "clear the previous address if answer no to 'have uk address'" in {
    val mockedJsonSerialiser = mock[JsonSerialiser]
    val mockedConfig = mock[Config]
    val mockedEncryptionService = mock[EncryptionService]
    val mockedAddressService = mock[AddressService]
    val mockedRemoteAssets = mock[RemoteAssets]

    val currentState = completeForcesApplication.copy(
      address = Some(LastUkAddress(Some(false),
        Some(PartialAddress(Some("123 Fake Street, Fakerton"), Some("123456789"), "WR26NJ", None)))),
      previousAddress = Some(PartialPreviousAddress(Some(MovedHouseOption.NotMoved), None))
    )
    val addressFirstStep = new AddressFirstStep(
      mockedJsonSerialiser,
      mockedConfig,
      mockedEncryptionService,
      mockedAddressService,
      mockedRemoteAssets
    )

    val result = addressFirstStep.clearPreviousAddress(currentState)

    result.previousAddress.isDefined should be (false)
  }
}
