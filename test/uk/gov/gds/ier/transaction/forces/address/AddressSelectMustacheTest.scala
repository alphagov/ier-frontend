package uk.gov.gds.ier.transaction.forces.address

import org.mockito.Mockito._
import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.service.{AddressService, WithAddressService}
import uk.gov.gds.ier.transaction.forces.InprogressForces

class AddressSelectMustacheTest
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with AddressForms
  with AddressSelectMustache
  with ErrorMessages
  with FormKeys
  with TestHelpers
  with WithSerialiser
  with WithAddressService {

  val serialiser = jsonSerialiser
  val addressService = mock[AddressService]

  when(addressService.lookupPartialAddress("WR26NJ")).thenReturn(List.empty)

  it should "empty progress form should produce empty Model (selectData)" in {

    val emptyApplicationForm =  addressForm
    val addressModel = mustache.data(
      emptyApplicationForm,
      Call("POST","/register-to-vote/forces/address/select"),
      InprogressForces()
    ).asInstanceOf[SelectModel]

    addressModel.question.title should be("What was your last UK address?")
    addressModel.question.postUrl should be("/register-to-vote/forces/address/select")

    addressModel.lookupUrl should be ("/register-to-vote/forces/address")
    addressModel.manualUrl should be ("/register-to-vote/forces/address/manual")
    addressModel.postcode.value should be ("")
    addressModel.possibleJsonList.value should be ("")
    addressModel.possiblePostcode.value should be ("")
    addressModel.hasAddresses should be (false)
  }

  it should "progress form with valid values should produce Mustache Model with values present (selectData)" in {

    val partiallyFilledApplicationForm = addressForm.fill(InprogressForces(
      address = Some(LastUkAddress(Some(true), Some(PartialAddress(
        addressLine = Some("Fake street 123"),
        uprn = Some("1234567"),
        postcode = "WR26NJ",
        manualAddress = None
      )))),
      possibleAddresses = None
    ))

    val addressModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST","/register-to-vote/forces/address/select"),
      InprogressForces()
    ).asInstanceOf[SelectModel]


    addressModel.question.title should be("What is your UK address?")
    addressModel.question.postUrl should be("/register-to-vote/forces/address/select")

    addressModel.lookupUrl should be ("/register-to-vote/forces/address")
    addressModel.manualUrl should be ("/register-to-vote/forces/address/manual")
    addressModel.postcode.value should be ("WR26NJ")
    addressModel.possiblePostcode.value should be ("WR26NJ")
    addressModel.hasAddresses should be (false)
  }
}
