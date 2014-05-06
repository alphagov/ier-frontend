package uk.gov.gds.ier.transaction.forces.previousAddress

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.specs2.mock.Mockito
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.{PartialAddress, MovedHouseOption, PartialPreviousAddress}
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.service.AddressService
import org.mockito.Mockito._

//class PreviousAddressPostcodeStepMockedTests extends FlatSpec
//  with TestHelpers
//  with Matchers
//  with Mockito {
//
//  it should "redirect to next question page if postcode is Northern Ireland" in {
//    val mockedJsonSerialiser = mock[JsonSerialiser]
//    val mockedConfig = mock[Config]
//    val mockedEncryptionService = mock[EncryptionService]
//    val mockedAddressService = mock[AddressService]
//
//    val addressStep = new PreviousAddressPostcodeStep(
//      serialiser = mockedJsonSerialiser,
//      config = mockedConfig,
//      encryptionService = mockedEncryptionService,
//      addressService = mockedAddressService)
//
//    val postcode = "bt7 1aa"
//
//    when (mockedAddressService.isNothernIreland(postcode)).thenReturn(true)
//    val currentState = completeForcesApplication.copy(
//      previousAddress = Some(PartialPreviousAddress(
//        movedRecently = Some(MovedHouseOption.Yes),
//        previousAddress = Some(PartialAddress(
//          addressLine = None,
//          uprn = None,
//          postcode = postcode,
//          manualAddress = None))
//      ))
//    )
//
//    val transferedState = addressStep.nextStep(currentState)
//    transferedState should be(controllers.step.forces.NationalityController.nationalityStep)
//  }
//}
