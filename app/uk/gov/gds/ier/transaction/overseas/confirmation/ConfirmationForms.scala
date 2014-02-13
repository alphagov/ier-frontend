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
      keys.name.key -> stepRequired(nameMapping),
      keys.previousName.key -> stepRequired(previousNameMapping),
      keys.previouslyRegistered.key -> stepRequired(previouslyRegisteredMapping),
      keys.dateLeftSpecial.key -> optional(dateLeftSpecialMapping),
      keys.dateLeftUk.key -> stepRequired(dateLeftUkMapping),
      "lastRegisteredToVote" -> optional(lastRegisteredToVoteMapping),
      keys.dob.key -> stepRequired(dobMapping),
      keys.nino.key -> stepRequired(ninoMapping),
      keys.lastUkAddress.key -> stepRequired(partialAddressMapping),
      keys.overseasAddress.key -> stepRequired(addressMapping),
      keys.openRegister.key -> stepRequired(optInMapping),
      keys.waysToVote.key -> stepRequired(waysToVoteMapping),
      keys.postalOrProxyVote.key -> stepRequired(postalOrProxyVoteMapping),
      keys.contact.key -> stepRequired(contactMapping),
      keys.passport.key -> optional(passportMapping),
      keys.possibleAddresses.key -> optional(possibleAddressesMapping)
    )
    (InprogressOverseas.apply)
    (InprogressOverseas.unapply)
    verifying (validateOverseasRenewerApplication)
  )

  lazy val validateOverseasRenewerApplication = Constraint[InprogressOverseas]("validateOverseasRenewerApplication") {
    application =>

      val validationErrors = mutable.MutableList[String]()

      if (application.dob.isDefined)
        validationErrors += keys.dob.key
      if (application.previouslyRegistered.exists(_.hasPreviouslyRegistered == true))
        validationErrors += keys.previouslyRegistered.key
      if (application.dateLeftUk.isDefined)
        validationErrors += keys.dateLeftUk.key
      if (application.lastUkAddress.isDefined)
        validationErrors += keys.lastUkAddress.key
      if (application.name.isDefined)
        validationErrors +=  keys.name.key
      if (application.previousName.isDefined)
        validationErrors += keys.previousName.key
      if (application.nino.isDefined)
        validationErrors +=   keys.nino.key
      if (application.address.isDefined)
        validationErrors +=  keys.address.key
      if (application.openRegisterOptin.isDefined)
        validationErrors += keys.openRegister.key
      if (application.waysToVote.isDefined)
        validationErrors += keys.waysToVote.key
      if (application.postalOrProxyVote.isDefined)
        validationErrors += keys.postalOrProxyVote.key
      if (application.contact.isDefined)
        validationErrors += keys.contact.key

      if (validationErrors.size > 0)
        Valid
      else
        Invalid ("Please complete this step", validationErrors)
  }
}
