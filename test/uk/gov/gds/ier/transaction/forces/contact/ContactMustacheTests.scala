package uk.gov.gds.ier.transaction.forces.contact

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.transaction.forces.InprogressForces

class ContactMustacheTests
  extends MustacheTestSuite
  with ContactForms
  with ContactMustache {

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = contactForm
    val contactModel = mustache.data(
      emptyApplicationForm,
      Call("POST", "/register-to-vote/forces/contact"),
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
      Call("POST", "/register-to-vote/forces/contact"),
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

  it should "prepopulate the email address from postal vote step - incorrect email variant" in {
    val partiallyFilledApplication =
      InprogressForces(
        postalOrProxyVote = Some(PostalOrProxyVote(
          typeVote = WaysToVoteType.ByProxy,
          postalVoteOption = Some(true),
          deliveryMethod = Some(PostalVoteDeliveryMethod(
            deliveryMethod = Some("email"),
            emailAddress = Some("invalid-email")
          ))
        ))
      )
    val partiallyFilledApplicationForm = contactForm.fill(partiallyFilledApplication)

    val contactModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/forces/contact"),
      InprogressForces()
    ).asInstanceOf[ContactModel]

    contactModel.question.title should be(
      "If we have questions about your application, how should we contact you?")
    contactModel.question.postUrl should be("/register-to-vote/forces/contact")

    contactModel.contactEmailCheckbox.attributes should be("")
    contactModel.contactEmailText.value should be("invalid-email")
    contactModel.contactPhoneCheckbox.attributes should be("")
    contactModel.contactPhoneText.value should be("")
    contactModel.contactPostCheckbox.attributes should be("")
  }

  it should "do NOT prepopulate the email address from postal vote step when contact detail email value already exists" in {
    val partiallyFilledApplication =
      InprogressForces(
        postalOrProxyVote = Some(PostalOrProxyVote(
          typeVote = WaysToVoteType.ByProxy,
          postalVoteOption = Some(true),
          deliveryMethod = Some(PostalVoteDeliveryMethod(
            deliveryMethod = Some("email"),
            emailAddress = Some("my@first.email.com")
          ))
        )),
        contact = Some(
          Contact(
            post = false,
            email = Some(ContactDetail(true, Some("my@second.email.com"))),
            phone = None
          )
        )
      )
    val partiallyFilledApplicationForm = contactForm.fill(partiallyFilledApplication)

    val contactModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/forces/contact"),
      InprogressForces()
    ).asInstanceOf[ContactModel]

    contactModel.question.title should be(
      "If we have questions about your application, how should we contact you?")
    contactModel.question.postUrl should be("/register-to-vote/forces/contact")

    contactModel.contactEmailCheckbox.attributes should be("checked=\"checked\"")
    contactModel.contactEmailText.value should be("my@second.email.com")
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
      Call("POST", "/register-to-vote/forces/contact"),
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
            phone = Some(ContactDetail(true, Some("1234567890ext123")))
          )
        )
      )
    val partiallyFilledApplicationForm = contactForm.fill(partiallyFilledApplication)

    val contactModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/forces/contact"),
      InprogressForces()
    ).asInstanceOf[ContactModel]

    contactModel.question.title should be("If we have questions about your application, how should we contact you?")
    contactModel.question.postUrl should be("/register-to-vote/forces/contact")

    contactModel.contactEmailCheckbox.attributes should be("")
    contactModel.contactEmailText.value should be("")
    contactModel.contactPhoneCheckbox.attributes should be("checked=\"checked\"")
    contactModel.contactPhoneText.value should be("1234567890ext123")
    contactModel.contactPostCheckbox.attributes should be("")
  }

  it should "progress form with filled phone and post option should produce Mustache Model with phone and post values present" in {
    val partiallyFilledApplication =
      InprogressForces(
        contact = Some(
          Contact(
            post = true,
            email = None,
            phone = Some(ContactDetail(true, Some("(+44)1234-567890")))
          )
        )
      )
    val partiallyFilledApplicationForm = contactForm.fill(partiallyFilledApplication)

    val contactModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/forces/contact"),
      InprogressForces()
    ).asInstanceOf[ContactModel]

    contactModel.question.title should be("If we have questions about your application, how should we contact you?")
    contactModel.question.postUrl should be("/register-to-vote/forces/contact")

    contactModel.contactEmailCheckbox.attributes should be("")
    contactModel.contactEmailText.value should be("")
    contactModel.contactPhoneCheckbox.attributes should be("checked=\"checked\"")
    contactModel.contactPhoneText.value should be("(+44)1234-567890")
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
      Call("POST", "/register-to-vote/forces/contact"),
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
