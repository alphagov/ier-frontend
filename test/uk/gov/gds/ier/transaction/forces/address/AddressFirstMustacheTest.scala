package uk.gov.gds.ier.transaction.forces.address

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.{WithMockRemoteAssets, TestHelpers}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.transaction.forces.InprogressForces

class AddressFirstMustacheTest
  extends FlatSpec
  with Matchers
  with AddressFirstForms
  with AddressForms
  with AddressFirstMustache
  with ErrorMessages
  with FormKeys
  with WithMockRemoteAssets
  with TestHelpers {

  self: FormKeys
    with ErrorMessages
    with WithSerialiser =>

  val serialiser = jsonSerialiser

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = addressFirstForm

    val addressFirstModel = mustache.data(
      emptyApplicationForm,
      Call("POST","url-string-1"),
      InprogressForces()
    ).asInstanceOf[AddressFirstModel]

    addressFirstModel.question.title should be("Do you have a UK address?")
    addressFirstModel.question.postUrl should be("url-string-1")

    addressFirstModel.hasAddressNo.attributes should be("")
    addressFirstModel.hasAddressYes.attributes should be("")

  }


  it should "progress form with valid values should produce Mustache Model with values present" in {
    val partiallyFilledApplicationForm = addressFirstForm.fill(InprogressForces(
      address = Some(LastUkAddress(
        hasUkAddress = Some(true),
        address = None
      ))
    ))

    val addressFirstModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST","url-string-1"),
      InprogressForces()
    ).asInstanceOf[AddressFirstModel]

    addressFirstModel.question.title should be("Do you have a UK address?")
    addressFirstModel.question.postUrl should be("url-string-1")

    addressFirstModel.hasAddressNo.attributes should be("")
    addressFirstModel.hasAddressYes.attributes should be("checked=\"checked\"")

  }
}
