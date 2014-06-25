package uk.gov.gds.ier.transaction.forces.contact

import scala.Some
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test._
import uk.gov.gds.ier.model._
import controllers.step.forces.routes._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.transaction.forces.InprogressForces

class ContactMustacheTests
  extends FlatSpec
  with Matchers
  with ContactForms
  with ContactMustache
  with ErrorMessages
  with FormKeys
  with TestHelpers
  with WithMockConfig
  with WithMockRemoteAssets
  with WithSerialiser {

  val serialiser = jsonSerialiser

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = contactForm
    val contactModel = mustache.data(
      emptyApplicationForm,
      ContactController.post,
      InprogressForces()
    ).asInstanceOf[ContactModel]

    contactModel.question.title should be("If we have questions about your application, how should we contact you?")
    contactModel.question.postUrl should be("/register-to-vote/forces/contact")

    contactModel.contactEmailCheckbox.attributes should be("")
    contactModel.contactEmailText.value should be("")
    contactModel.contactPhoneCheckbox.attributes should be("")
    contactModel.contactPhoneText.value should be("")
    contactModel.contactPostCheckbox.attributes should be("")
  }

  it should "prepopulate the email address from postal vote step" in {
    val partiallyFilledApplication =
      InprogressForces(
        postalOrProxyVote = Some(PostalOrProxyVote(
          typeVote = WaysToVoteType.ByProxy,
          postalVoteOption = Some(true),
          deliveryMethod = Some(PostalVoteDeliveryMethod(
            deliveryMethod = Some("email"),
            emailAddress = Some("my@email.com")
          ))
        ))
      )
    val partiallyFilledApplicationForm = contactForm.fill(partiallyFilledApplication)

    val contactModel = mustache.data(
      partiallyFilledApplicationForm,
      ContactController.post,
      InprogressForces()
    ).asInstanceOf[ContactModel]

    contactModel.question.title should be(
      "If we have questions about your application, how should we contact you?")
    contactModel.question.postUrl should be("/register-to-vote/forces/contact")

    contactModel.contactEmailCheckbox.attributes should be("")
    contactModel.contactEmailText.value should be("my@email.com")
    contactModel.contactPhoneCheckbox.attributes should be("")
    contactModel.contactPhoneText.value should be("")
    contactModel.contactPostCheckbox.attributes should be("")
  }

  it should "progress form with filled email should produce Mustache Model with email value present" in {
    val partiallyFilledApplication =
      InprogressForces(
        contact = Some(
          Contact(
            post = false,
            email = Some(ContactDetail(true, Some("my@email.com"))),
            phone = None
          )
        )
      )
    val partiallyFilledApplicationForm = contactForm.fill(partiallyFilledApplication)

    val contactModel = mustache.data(
      partiallyFilledApplicationForm,
      ContactController.post,
      InprogressForces()
    ).asInstanceOf[ContactModel]

    contactModel.question.title should be("If we have questions about your application, how should we contact you?")
    contactModel.question.postUrl should be("/register-to-vote/forces/contact")

    contactModel.contactEmailCheckbox.attributes should be("checked=\"checked\"")
    contactModel.contactEmailText.value should be("my@email.com")
    contactModel.contactPhoneCheckbox.attributes should be("")
    contactModel.contactPhoneText.value should be("")
    contactModel.contactPostCheckbox.attributes should be("")
  }

  it should "progress form with filled phone should produce Mustache Model with phone value present" in {
    val partiallyFilledApplication =
      InprogressForces(
        contact = Some(
          Contact(
            post = false,
            email = None,
            phone = Some(ContactDetail(true, Some("1234567890")))
          )
        )
      )
    val partiallyFilledApplicationForm = contactForm.fill(partiallyFilledApplication)

    val contactModel = mustache.data(
      partiallyFilledApplicationForm,
      ContactController.post,
      InprogressForces()
    ).asInstanceOf[ContactModel]

    contactModel.question.title should be("If we have questions about your application, how should we contact you?")
    contactModel.question.postUrl should be("/register-to-vote/forces/contact")

    contactModel.contactEmailCheckbox.attributes should be("")
    contactModel.contactEmailText.value should be("")
    contactModel.contactPhoneCheckbox.attributes should be("checked=\"checked\"")
    contactModel.contactPhoneText.value should be("1234567890")
    contactModel.contactPostCheckbox.attributes should be("")
  }

  it should "progress form with filled phone and post option should produce Mustache Model with phone and post values present" in {
    val partiallyFilledApplication =
      InprogressForces(
        contact = Some(
          Contact(
            post = true,
            email = None,
            phone = Some(ContactDetail(true, Some("1234567890")))
          )
        )
      )
    val partiallyFilledApplicationForm = contactForm.fill(partiallyFilledApplication)

    val contactModel = mustache.data(
      partiallyFilledApplicationForm,
      ContactController.post,
      InprogressForces()
    ).asInstanceOf[ContactModel]

    contactModel.question.title should be("If we have questions about your application, how should we contact you?")
    contactModel.question.postUrl should be("/register-to-vote/forces/contact")

    contactModel.contactEmailCheckbox.attributes should be("")
    contactModel.contactEmailText.value should be("")
    contactModel.contactPhoneCheckbox.attributes should be("checked=\"checked\"")
    contactModel.contactPhoneText.value should be("1234567890")
    contactModel.contactPostCheckbox.attributes should be("checked=\"checked\"")
  }

  it should "progress form with validation errors should produce Model with error list present" in {
    val partiallyFilledApplication =
      InprogressForces(
        contact = Some(
          Contact(
            post = false,
            email = None,
            phone = Some(ContactDetail(true, None))
          )
        )
      )
    val partiallyFilledApplicationForm = contactForm.fillAndValidate(partiallyFilledApplication)

    val contactModel = mustache.data(
      partiallyFilledApplicationForm,
      ContactController.post,
      InprogressForces()
    ).asInstanceOf[ContactModel]

    contactModel.question.title should be("If we have questions about your application, how should we contact you?")
    contactModel.question.postUrl should be("/register-to-vote/forces/contact")

    contactModel.contactEmailCheckbox.attributes should be("")
    contactModel.contactEmailText.value should be("")
    contactModel.contactPhoneCheckbox.attributes should be("checked=\"checked\"")
    contactModel.contactPhoneText.value should be("")
    contactModel.contactPostCheckbox.attributes should be("")

    contactModel.question.errorMessages.mkString(", ") should be("Please enter your phone number")
  }
}
