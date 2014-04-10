package uk.gov.gds.ier.transaction.ordinary.address

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.specs2.mock.Mockito
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.PartialAddress
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.service.AddressService

class AddressControllerTests extends FlatSpec with TestHelpers with Matchers with Mockito {

  it should "redirect to Northern Ireland exit page if the postcode starts with BT" in {
    val mockedJsonSerialiser = mock[JsonSerialiser]
    val mockedConfig = mock[Config]
    val mockedEncryptionService = mock[EncryptionService]
    val mockedAddressService = mock[AddressService]

    val addressStep = new AddressStep(mockedJsonSerialiser, mockedConfig,
        mockedEncryptionService, mockedAddressService)

    val currentState = completeOrdinaryApplication.copy(
    		address = Some(PartialAddress(Some("123 Fake Street, Fakerton"), Some("123456789"), 
    				"BTabc", None, None)))

    val transferedState = addressStep.nextStep(currentState)
    println ("hello : " + transferedState)
  }
  
  it should "redirect to Scotland exit page if the gssCode starts with S" in {
    val mockedJsonSerialiser = mock[JsonSerialiser]
    val mockedConfig = mock[Config]
    val mockedEncryptionService = mock[EncryptionService]
    val mockedAddressService = mock[AddressService]

    val addressStep = new AddressStep(mockedJsonSerialiser, mockedConfig,
        mockedEncryptionService, mockedAddressService)

    val currentState = completeOrdinaryApplication.copy(
    		address = Some(PartialAddress(Some("123 Fake Street, Fakerton"), 
    				Some("123456789"), "abcd", None, Some("S123"))))

    val transferedState = addressStep.nextStep(currentState)
    println ("hello : " + transferedState)
  }
}
