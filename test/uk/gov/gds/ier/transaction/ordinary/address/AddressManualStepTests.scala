package uk.gov.gds.ier.transaction.ordinary.address

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.specs2.mock.Mockito
import org.scalatest.mock.MockitoSugar
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.PartialAddress
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.service.AddressService
import controllers.routes.ExitController
import uk.gov.gds.ier.step.GoTo
import org.mockito.Mockito._
class AddressManualStepTests extends FlatSpec with TestHelpers with Matchers with Mockito {

  it should "redirect to Scotland exit page if the gssCode starts with S" in {
    val mockedJsonSerialiser = mock[JsonSerialiser]
    val mockedConfig = mock[Config]
    val mockedEncryptionService = mock[EncryptionService]
    val mockedAddressService = mock[AddressService]

    val addressManualStep = new AddressManualStep(mockedJsonSerialiser, mockedConfig,
        mockedEncryptionService, mockedAddressService)
    
    val postcode = "EH1 1AA"
    val addresses = List(PartialAddress(None, None, postcode = postcode, None, gssCode = Some("S1")), 
        PartialAddress(None, None, postcode = postcode, None, gssCode = Some("S2")))
    when(mockedAddressService.lookupPartialAddress(postcode)).thenReturn(addresses)

    val currentState = completeOrdinaryApplication.copy(
    		address = Some(PartialAddress(None, None, "EH1 1AA", None, None)))

    val transferedState = addressManualStep.nextStep(currentState)
    transferedState should be (GoTo(ExitController.scotland))
  }
}
