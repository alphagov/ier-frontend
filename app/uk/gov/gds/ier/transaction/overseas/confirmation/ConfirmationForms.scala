package uk.gov.gds.ier.transaction.overseas.confirmation

import play.api.data.Forms._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.{ErrorTransformForm, FormKeys, ErrorMessages}
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.transaction.overseas.lastUkAddress.LastUkAddressForms
import uk.gov.gds.ier.transaction.overseas.previouslyRegistered.PreviouslyRegisteredForms
import uk.gov.gds.ier.transaction.overseas.dateLeftSpecial.DateLeftSpecialForms
import uk.gov.gds.ier.transaction.overseas.dateLeftUk.DateLeftUkForms
import uk.gov.gds.ier.transaction.overseas.dateOfBirth.DateOfBirthForms
import uk.gov.gds.ier.transaction.overseas.lastRegisteredToVote.LastRegisteredToVoteForms
import uk.gov.gds.ier.transaction.overseas.nino.NinoForms
import uk.gov.gds.ier.transaction.overseas.name.NameForms
import uk.gov.gds.ier.transaction.overseas.parentName.ParentNameForms
import uk.gov.gds.ier.transaction.overseas.openRegister.OpenRegisterForms
import uk.gov.gds.ier.transaction.overseas.contact.ContactForms
import uk.gov.gds.ier.transaction.overseas.passport.PassportForms
import uk.gov.gds.ier.transaction.overseas.address.AddressForms
import uk.gov.gds.ier.transaction.overseas.waysToVote.WaysToVoteForms
import uk.gov.gds.ier.transaction.overseas.applicationFormVote.PostalOrProxyVoteForms
import scala.collection.mutable

trait ConfirmationForms
  extends FormKeys
  with ErrorMessages
  with WithSerialiser
  with PreviouslyRegisteredForms
  with DateLeftSpecialForms
  with DateLeftUkForms
  with ParentNameForms
  with DateOfBirthForms
  with LastRegisteredToVoteForms
  with NinoForms
  with AddressForms
  with LastUkAddressForms
  with OpenRegisterForms
  with NameForms
  with PassportForms
  with WaysToVoteForms
  with PostalOrProxyVoteForms
  with ContactForms
  with CommonConstraints {

  val stubMapping = mapping(
    "foo" -> text
  ) (foo => Stub()) (stub => Some("foo"))

  val optInMapping = single(
    keys.optIn.key -> boolean
  )

  val confirmationForm = ErrorTransformForm(
    mapping(
      keys.name.key -> optional(nameMapping),
      keys.previousName.key -> optional(previousNameMapping),
      keys.previouslyRegistered.key -> optional(previouslyRegisteredMapping),
      keys.dateLeftSpecial.key -> optional(dateLeftSpecialMapping),
      keys.dateLeftUk.key -> optional(dateLeftUkMapping),
      keys.parentName.key -> optional(parentNameMapping),
      keys.parentPreviousName.key -> optional(parentPrevNameMapping),
      "lastRegisteredToVote" -> optional(lastRegisteredToVoteMapping),
      keys.dob.key -> optional(dobMapping),
      keys.nino.key -> optional(ninoMapping),
      keys.lastUkAddress.key -> optional(partialAddressMapping),
      keys.overseasAddress.key -> optional(addressMapping),
      keys.openRegister.key -> optional(optInMapping),
      keys.waysToVote.key -> optional(waysToVoteMapping),
      keys.postalOrProxyVote.key -> optional(postalOrProxyVoteMapping),
      keys.contact.key -> optional(contactMapping),
      keys.passport.key -> optional(passportMapping),
      keys.possibleAddresses.key -> optional(possibleAddressesMapping)
    )
    (InprogressOverseas.apply)
    (InprogressOverseas.unapply)
    verifying (validateOverseasRenewerApplication)
  )

  lazy val validateOverseasRenewerApplication = Constraint[InprogressOverseas]("validateOverseasRenewerApplication") {
    application =>

      val validationErrors = Seq (
          if (!application.dob.isDefined)
            Some(keys.dob) else None,
          if (!application.previouslyRegistered.exists(_.hasPreviouslyRegistered == true))
            Some(keys.previouslyRegistered) else None,
          if (!application.dateLeftUk.isDefined)
            Some(keys.dateLeftUk) else None,
          if (!application.lastUkAddress.isDefined)
            Some(keys.lastUkAddress) else None,
          if (!application.name.isDefined)
            Some(keys.name) else None,
          if (!application.previousName.isDefined)
            Some(keys.previousName) else None,
          if (!application.nino.isDefined)
            Some(keys.nino) else None,
          if (!application.address.isDefined)
            Some(keys.overseasAddress) else None,
          if (!application.openRegisterOptin.isDefined)
            Some(keys.openRegister) else None,
          if (!application.waysToVote.isDefined)
            Some(keys.waysToVote) else None,
          if (!application.postalOrProxyVote.isDefined)
            Some(keys.postalOrProxyVote) else None,
          if (!application.contact.isDefined)
            Some(keys.contact) else None
        ).flatten

      if (validationErrors.size == 0)
        Valid
      else
        Invalid ("Please complete this step", validationErrors:_*)
  }
}
