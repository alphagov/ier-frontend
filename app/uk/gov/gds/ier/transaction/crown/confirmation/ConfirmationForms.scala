package uk.gov.gds.ier.transaction.crown.confirmation

import play.api.data.Forms._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.{ErrorTransformForm, FormKeys, ErrorMessages}
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.transaction.crown.statement.StatementForms
import uk.gov.gds.ier.transaction.crown.nationality.NationalityForms
import uk.gov.gds.ier.transaction.crown.dateOfBirth.DateOfBirthForms
import uk.gov.gds.ier.transaction.crown.name.NameForms
import uk.gov.gds.ier.transaction.crown.nino.NinoForms
import uk.gov.gds.ier.transaction.crown.address.AddressForms
import uk.gov.gds.ier.transaction.crown.contactAddress.ContactAddressForms
import uk.gov.gds.ier.transaction.crown.openRegister.OpenRegisterForms
import uk.gov.gds.ier.transaction.crown.waysToVote.WaysToVoteForms
import uk.gov.gds.ier.transaction.crown.applicationFormVote.PostalOrProxyVoteForms
import uk.gov.gds.ier.transaction.crown.contact.ContactForms
import uk.gov.gds.ier.transaction.crown.job.JobForms

trait ConfirmationForms
  extends FormKeys
  with ErrorMessages
  with WithSerialiser
  with StatementForms
  with AddressForms
  with NationalityForms
  with DateOfBirthForms
  with NameForms
  with NinoForms
  with JobForms
  with ContactAddressForms
  with OpenRegisterForms
  with WaysToVoteForms
  with PostalOrProxyVoteForms
  with ContactForms
  with CommonConstraints {

  val confirmationForm = ErrorTransformForm(
    mapping(
      keys.statement.key -> stepRequired(statementMapping),
      keys.address.key -> stepRequired(partialAddressMapping),
      keys.nationality.key -> stepRequired(nationalityMapping),
      keys.dob.key -> stepRequired(dobAndReasonMapping),
      keys.name.key -> stepRequired(nameMapping),
      keys.job.key -> stepRequired(jobMapping),
      keys.nino.key -> stepRequired(ninoMapping),
      keys.contactAddress.key -> stepRequired(contactAddressMapping),
      keys.openRegister.key -> stepRequired(openRegisterOptInMapping),
      keys.waysToVote.key -> stepRequired(waysToVoteMapping),
      keys.postalOrProxyVote.key -> stepRequired(postalOrProxyVoteMapping),
      keys.contact.key -> stepRequired(contactMapping),
      keys.possibleAddresses.key -> optional(possibleAddressesMapping)
    )
    (InprogressCrown.apply)
    (InprogressCrown.unapply)
  )
}
