package uk.gov.gds.ier.transaction.ordinary.contact

import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.data.Form
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.ContactConstraints
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait ContactForms extends ContactConstraints {
  self:  FormKeys
    with ErrorMessages
    with WithSerialiser =>

  val contactForm = ErrorTransformForm(
    mapping(
      keys.contact.key -> optional(Contact.mapping),
      keys.postalVote.key -> optional(PostalVote.mapping)
    ) (
      (contact, postalVote) => InprogressOrdinary(
        postalVote = postalVote,
        contact = contact
      )
    ) (
      inprogress => Some(
        inprogress.contact,
        inprogress.postalVote
      )
    ).verifying(
      atLeastOneOptionSelectedOrdinary,
      numberProvidedIfPhoneSelected,
      emailProvidedIfEmailSelected,
      emailIsValidIfProvided
    )
  )
}

