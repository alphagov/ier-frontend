package uk.gov.gds.ier.step.contact

import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{InprogressApplication, Contact}
import uk.gov.gds.ier.validation.Constraints
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.data.Form
import play.api.data.Forms._

trait ContactForms {
  self:  FormKeys
    with ErrorMessages
    with WithSerialiser
    with Constraints =>
  
  lazy val contactMapping = mapping(
    keys.contactType.key -> text
      .verifying("Please select a contact method", 
        method => List("phone", "post", "text", "email").contains(method)),
    keys.post.key -> optional(nonEmptyText
      .verifying(postMaxLengthError, _.size <= maxTextFieldLength)),
    keys.phone.key -> optional(nonEmptyText
      .verifying(postMaxLengthError, _.size <= maxTextFieldLength)),
    keys.textNum.key -> optional(nonEmptyText
      .verifying(postMaxLengthError, _.size <= maxTextFieldLength)),
    keys.email.key -> optional(nonEmptyText
      .verifying(postMaxLengthError, _.size <= maxTextFieldLength))
  ) (
    Contact.apply
  ) (
    Contact.unapply
  ).verifying(contactEmailConstraint, contactTelephoneConstraint, contactTextConstraint)

  val contactForm = Form(
    mapping(
      keys.contact.key -> optional(contactMapping)
        .verifying("Please answer this question", _.isDefined)
    ) (
      contact => InprogressApplication(contact = contact)
    ) (
      inprogress => Some(inprogress.contact)
    )
  )
}

