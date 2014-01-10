package uk.gov.gds.ier.step.contact

import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.data.Form
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.ContactConstraints
import uk.gov.gds.ier.validation.Key
import scala.Some
import uk.gov.gds.ier.validation.Key
import uk.gov.gds.ier.model.ContactDetail
import scala.Some
import uk.gov.gds.ier.model.Contact

trait ContactForms extends ContactConstraints {
  self:  FormKeys
    with ErrorMessages
    with WithSerialiser =>

  def contactMeMapping(key:Key, name:String) = mapping(
    keys.contactMe.key -> boolean,
    keys.detail.key -> optional(text)
  ) (ContactDetail.apply) (ContactDetail.unapply).verifying(detailFilled(key.detail, name))

  def contactDetailMapping(key:Key, name:String) = {
    contactMeMapping(key:Key, name:String).transform(
      (contact) => if (contact.contactMe) contact.detail else None,
      (detail:Option[String]) => ContactDetail(detail.isDefined, detail)
    )
  }

  lazy val postDetailMapping = mapping(
    keys.contactMe.key -> optional(boolean)
  ) (_.getOrElse(false)) (post => Some(Some(post)))

  lazy val contactMapping = mapping(
    keys.post.key -> postDetailMapping,
    keys.phone.key -> contactDetailMapping(keys.contact.phone, "phone number"),
    keys.textNum.key -> contactDetailMapping(keys.contact.textNum, "phone number"),
    keys.email.key -> contactDetailMapping(keys.contact.email, "email address")
  ) (
    Contact.apply
  ) (
    Contact.unapply
  )

  val contactForm = ErrorTransformForm(
    mapping(
      keys.contact.key -> optional(contactMapping)
        .verifying("Please answer this question", _.isDefined)
    ) (
      contact => InprogressOrdinary(contact = contact)
    ) (
      inprogress => Some(inprogress.contact)
    )
  )
}

