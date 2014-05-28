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
class AddressManualStepMockedTests extends FlatSpec with TestHelpers with Matchers with Mockito {

  it should "clear the address line and uprn if an manual address is filled in" in {
    val mockedJsonSerialiser = mock[JsonSerialiser]
    val mockedConfig = mock[Config]
    val mockedEncryptionService = mock[EncryptionService]
    val mockedRemoteAssets = mock[RemoteAssets]

    val partialAddress = PartialAddress(Some("123 Fake Street, Fakerton"), Some("123456789"), "WR26NJ",
        Some(PartialManualAddress(Some("line1"), Some("line2"), Some("line3"), Some("city"))))

    val currentState = completeForcesApplication.copy(
      address = Some(LastUkAddress(
        hasUkAddress = Some(HasAddressOption.No),
        address = Some(partialAddress)
      ))
    )
    val addressManualStep = new AddressManualStep(
      mockedJsonSerialiser,
      mockedConfig,
      mockedEncryptionService,
      mockedRemoteAssets
    )

    val result = addressManualStep.clearAddressAndUprn(currentState)
    val expected = result.address.exists{addr =>
      addr.address.exists{pAddr =>
        pAddr.addressLine.isEmpty && pAddr.uprn.isEmpty
      }
    }

    expected should be (true)

  }
}
