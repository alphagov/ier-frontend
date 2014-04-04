package uk.gov.gds.ier.transaction.forces.contactAddress

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import scala.Some
import controllers.step.forces.routes.{OpenRegisterController, ContactAddressController}
import uk.gov.gds.ier.model.{PartialAddress, PossibleContactAddresses, ContactAddress}
import uk.gov.gds.ier.transaction.forces.InprogressForces

class ContactAddressMustacheTest
  extends FlatSpec
  with Matchers
  with ContactAddressForms
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val contactAddressMustache = new ContactAddressMustache {}

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = contactAddressForm

    val contactAddressModel = contactAddressMustache.transformFormStepToMustacheData(
        emptyApplicationForm,
        ContactAddressController.post,
        Some(OpenRegisterController.get))

    contactAddressModel.question.title should be("Where should we write to you about your registration?")
    contactAddressModel.question.postUrl should be("/register-to-vote/forces/contact-address")
    contactAddressModel.question.backUrl should be("/register-to-vote/forces/open-register")

    contactAddressModel.otherAddress.otherAddressOption.attributes should be("")
    contactAddressModel.otherAddress.otherAddressLine1.value should be("")
    contactAddressModel.otherAddress.otherAddressLine2.value should be("")
    contactAddressModel.otherAddress.otherAddressLine3.value should be("")
    contactAddressModel.otherAddress.otherAddressLine4.value should be("")
    contactAddressModel.otherAddress.otherAddressLine5.value should be("")
    contactAddressModel.otherAddress.otherAddressPostcode.value should be("")
    contactAddressModel.otherAddress.otherAddressCountry.value should be("")

    contactAddressModel.bfpoAddress.BFPOAddressOption.attributes should be("")
    contactAddressModel.bfpoAddress.BFPOAddressLine1.value should be("")
    contactAddressModel.bfpoAddress.BFPOAddressLine2.value should be("")
    contactAddressModel.bfpoAddress.BFPOAddressLine3.value should be("")
    contactAddressModel.bfpoAddress.BFPOAddressLine4.value should be("")
    contactAddressModel.bfpoAddress.BFPOAddressLine5.value should be("")
    contactAddressModel.bfpoAddress.BFPOAddressPostcode.value should be("")

    contactAddressModel.ukAddress.ukAddressOption.attributes should be("")
    contactAddressModel.ukAddress.ukAddressLineText.value should be("")

  }

  it should "fully filled applicant UK contact address should produce Mustache Model with dob values present" in {
    val filledForm = contactAddressForm.fillAndValidate(InprogressForces(
      contactAddress = Some(PossibleContactAddresses(
        contactAddressType = Some("uk"),
        ukAddressLine = Some("uk address text line"),
        bfpoContactAddress = None,
        otherContactAddress= None
      ))
    ))

    val contactAddressModel = contactAddressMustache.transformFormStepToMustacheData(
      filledForm,
      ContactAddressController.post,
      Some(OpenRegisterController.get))

    contactAddressModel.question.title should be("Where should we write to you about your registration?")
    contactAddressModel.question.postUrl should be("/register-to-vote/forces/contact-address")
    contactAddressModel.question.backUrl should be("/register-to-vote/forces/open-register")

    contactAddressModel.otherAddress.otherAddressOption.attributes should be("")
    contactAddressModel.otherAddress.otherAddressLine1.value should be("")
    contactAddressModel.otherAddress.otherAddressLine2.value should be("")
    contactAddressModel.otherAddress.otherAddressLine3.value should be("")
    contactAddressModel.otherAddress.otherAddressLine4.value should be("")
    contactAddressModel.otherAddress.otherAddressLine5.value should be("")
    contactAddressModel.otherAddress.otherAddressPostcode.value should be("")
    contactAddressModel.otherAddress.otherAddressCountry.value should be("")

    contactAddressModel.bfpoAddress.BFPOAddressOption.attributes should be("")
    contactAddressModel.bfpoAddress.BFPOAddressLine1.value should be("")
    contactAddressModel.bfpoAddress.BFPOAddressLine2.value should be("")
    contactAddressModel.bfpoAddress.BFPOAddressLine3.value should be("")
    contactAddressModel.bfpoAddress.BFPOAddressLine4.value should be("")
    contactAddressModel.bfpoAddress.BFPOAddressLine5.value should be("")
    contactAddressModel.bfpoAddress.BFPOAddressPostcode.value should be("")

    contactAddressModel.ukAddress.ukAddressOption.attributes should be("checked=\"checked\"")
    contactAddressModel.ukAddress.ukAddressLineText.value should be("uk address text line")
  }

  it should "fully filled applicant BFPO contact address should produce Mustache Model with dob values present" in {
    val filledForm = contactAddressForm.fillAndValidate(InprogressForces(
      contactAddress = Some(PossibleContactAddresses(
        contactAddressType = Some("bfpo"),
        ukAddressLine = None,
        bfpoContactAddress = Some(ContactAddress(
          country = None,
          postcode = Some("BFPO90"),
          addressLine1 = Some("Address line 1"),
          addressLine2 = Some("Address line 2"),
          addressLine3 = Some("Address line 3"),
          addressLine4 = None,
          addressLine5 = None
        )),
        otherContactAddress= None
      ))
    ))

    val contactAddressModel = contactAddressMustache.transformFormStepToMustacheData(
      filledForm,
      ContactAddressController.post,
      Some(OpenRegisterController.get))

    contactAddressModel.question.title should be("Where should we write to you about your registration?")
    contactAddressModel.question.postUrl should be("/register-to-vote/forces/contact-address")
    contactAddressModel.question.backUrl should be("/register-to-vote/forces/open-register")

    contactAddressModel.otherAddress.otherAddressOption.attributes should be("")
    contactAddressModel.otherAddress.otherAddressLine1.value should be("")
    contactAddressModel.otherAddress.otherAddressLine2.value should be("")
    contactAddressModel.otherAddress.otherAddressLine3.value should be("")
    contactAddressModel.otherAddress.otherAddressLine4.value should be("")
    contactAddressModel.otherAddress.otherAddressLine5.value should be("")
    contactAddressModel.otherAddress.otherAddressPostcode.value should be("")
    contactAddressModel.otherAddress.otherAddressCountry.value should be("")

    contactAddressModel.bfpoAddress.BFPOAddressOption.attributes should be("checked=\"checked\"")
    contactAddressModel.bfpoAddress.BFPOAddressLine1.value should be("Address line 1")
    contactAddressModel.bfpoAddress.BFPOAddressLine2.value should be("Address line 2")
    contactAddressModel.bfpoAddress.BFPOAddressLine3.value should be("Address line 3")
    contactAddressModel.bfpoAddress.BFPOAddressLine4.value should be("")
    contactAddressModel.bfpoAddress.BFPOAddressLine5.value should be("")
    contactAddressModel.bfpoAddress.BFPOAddressPostcode.value should be("BFPO90")

    contactAddressModel.ukAddress.ukAddressOption.attributes should be("")
    contactAddressModel.ukAddress.ukAddressLineText.value should be("")
  }

  it should "fully filled applicant other contact address should produce Mustache Model with dob values present" in {
    val filledForm = contactAddressForm.fillAndValidate(InprogressForces(
      contactAddress = Some(PossibleContactAddresses(
        contactAddressType = Some("other"),
        ukAddressLine = None,
        bfpoContactAddress = None,
        otherContactAddress=  Some(ContactAddress(
          country = Some("Spain"),
          postcode = Some("08191"),
          addressLine1 = Some("Address line 1"),
          addressLine2 = Some("Address line 2"),
          addressLine3 = Some("Address line 3"),
          addressLine4 = None,
          addressLine5 = None
        ))
      ))
    ))

    val contactAddressModel = contactAddressMustache.transformFormStepToMustacheData(
      filledForm,
      ContactAddressController.post,
      Some(OpenRegisterController.get))

    contactAddressModel.question.title should be("Where should we write to you about your registration?")
    contactAddressModel.question.postUrl should be("/register-to-vote/forces/contact-address")
    contactAddressModel.question.backUrl should be("/register-to-vote/forces/open-register")

    contactAddressModel.otherAddress.otherAddressOption.attributes should be("checked=\"checked\"")
    contactAddressModel.otherAddress.otherAddressLine1.value should be("Address line 1")
    contactAddressModel.otherAddress.otherAddressLine2.value should be("Address line 2")
    contactAddressModel.otherAddress.otherAddressLine3.value should be("Address line 3")
    contactAddressModel.otherAddress.otherAddressLine4.value should be("")
    contactAddressModel.otherAddress.otherAddressLine5.value should be("")
    contactAddressModel.otherAddress.otherAddressPostcode.value should be("08191")
    contactAddressModel.otherAddress.otherAddressCountry.value should be("Spain")

    contactAddressModel.bfpoAddress.BFPOAddressOption.attributes should be("")
    contactAddressModel.bfpoAddress.BFPOAddressLine1.value should be("")
    contactAddressModel.bfpoAddress.BFPOAddressLine2.value should be("")
    contactAddressModel.bfpoAddress.BFPOAddressLine3.value should be("")
    contactAddressModel.bfpoAddress.BFPOAddressLine4.value should be("")
    contactAddressModel.bfpoAddress.BFPOAddressLine5.value should be("")
    contactAddressModel.bfpoAddress.BFPOAddressPostcode.value should be("")

    contactAddressModel.ukAddress.ukAddressOption.attributes should be("")
    contactAddressModel.ukAddress.ukAddressLineText.value should be("")
  }
}
