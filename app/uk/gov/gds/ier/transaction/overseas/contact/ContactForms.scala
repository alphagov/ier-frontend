package uk.gov.gds.ier.transaction.overseas.contact

import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.ContactConstraints
import uk.gov.gds.ier.validation.Key
import uk.gov.gds.ier.model.{InprogressOverseas, ContactDetail, Contact}
import scala.Some

trait ContactForms extends ContactConstraints {
  self:  FormKeys
    with ErrorMessages
    with WithSerialiser =>

  def contactMeMapping(key:Key, name:String) = mapping(
    keys.contactMe.key -> boolean,
    keys.detail.key -> optional(text)
  ) (ContactDetail.apply) (ContactDetail.unapply).verifying(detailFilled(key.detail, name))

  lazy val postDetailMapping = mapping(
    keys.contactMe.key -> optional(boolean)
  ) (_.getOrElse(false)) (post => Some(Some(post)))

  lazy val contactMapping = mapping(
    keys.post.key -> postDetailMapping,
    keys.phone.key -> optional(contactMeMapping(keys.contact.phone, "phone number")),
    keys.email.key -> optional(contactMeMapping(keys.contact.email, "email address"))
  ) (
    Contact.apply
  ) (
    Contact.unapply
  ).verifying (emailIsValid)

  val contactForm = ErrorTransformForm(
    mapping(
      keys.contact.key -> optional(contactMapping)
    ) (
      contact => InprogressOverseas(contact = contact)
    ) (
      inprogress => Some(inprogress.contact)
    ).verifying (formIsValidOverseas)
  )
}

