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
    addressFirstModel.hasAddressYesLivingThere.attributes should be("")
    addressFirstModel.hasAddressYesNotLivingThere.attributes should be("")

  }


  it should "mark no checked when hasAddress = no" in {
    val partiallyFilledApplicationForm = addressFirstForm.fill(InprogressForces(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.No),
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

    addressFirstModel.hasAddressNo.attributes should be("checked=\"checked\"")
    addressFirstModel.hasAddressYesLivingThere.attributes should be("")
    addressFirstModel.hasAddressYesNotLivingThere.attributes should be("")

  }

  it should "mark yes living there as checked when hasAddress = yesAndLivingThere" in {
    val partiallyFilledApplicationForm = addressFirstForm.fill(InprogressForces(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.YesAndLivingThere),
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
    addressFirstModel.hasAddressYesLivingThere.attributes should be("checked=\"checked\"")
    addressFirstModel.hasAddressYesNotLivingThere.attributes should be("")

  }

  it should "mark yes not living there as checked when hasAddress = yesAndNotLivingThere" in {
    val partiallyFilledApplicationForm = addressFirstForm.fill(InprogressForces(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.YesAndNotLivingThere),
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
    addressFirstModel.hasAddressYesLivingThere.attributes should be("")
    addressFirstModel.hasAddressYesNotLivingThere.attributes should be("checked=\"checked\"")

  }
}
