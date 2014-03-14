package uk.gov.gds.ier.transaction.forces.confirmation

import play.api.data.Forms._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.{ErrorTransformForm, FormKeys, ErrorMessages}
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.transaction.forces.statement.StatementForms
import uk.gov.gds.ier.transaction.forces.address.AddressForms
import uk.gov.gds.ier.transaction.forces.nationality.NationalityForms
import uk.gov.gds.ier.transaction.forces.dateOfBirth.DateOfBirthForms
import uk.gov.gds.ier.transaction.forces.name.NameForms
import uk.gov.gds.ier.transaction.forces.nino.NinoForms
import uk.gov.gds.ier.transaction.forces.service.ServiceForms
import uk.gov.gds.ier.transaction.forces.rank.RankForms
import uk.gov.gds.ier.transaction.forces.contactAddress.ContactAddressForms
import uk.gov.gds.ier.transaction.forces.openRegister.OpenRegisterForms
import uk.gov.gds.ier.transaction.forces.waysToVote.WaysToVoteForms
import uk.gov.gds.ier.transaction.forces.applicationFormVote.PostalOrProxyVoteForms
import uk.gov.gds.ier.transaction.forces.contact.ContactForms
import play.api.data.validation.Constraint
import play.api.data.validation.Invalid
import play.api.data.validation.Valid

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
  with ServiceForms
  with RankForms
  with ContactAddressForms
  with OpenRegisterForms
  with WaysToVoteForms
  with PostalOrProxyVoteForms
  with ContactForms
  with CommonConstraints {

  val confirmationForm = ErrorTransformForm(
    mapping(
      keys.statement.key -> optional(statementMapping),
      keys.address.key -> optional(partialAddressMapping),
      keys.nationality.key -> optional(nationalityMapping),
      keys.dob.key -> optional(dobAndReasonMapping),
      keys.name.key -> optional(nameMapping),
      keys.nino.key -> optional(ninoMapping),
      keys.service.key -> optional(serviceMapping),
      keys.rank.key -> optional(rankMapping),
      keys.contactAddress.key -> optional(possibleContactAddressesMapping),
      keys.openRegister.key -> optional(openRegisterOptInMapping),
      keys.waysToVote.key -> optional(waysToVoteMapping),
      keys.postalOrProxyVote.key -> optional(postalOrProxyVoteMapping),
      keys.contact.key -> optional(contactMapping),
      keys.possibleAddresses.key -> optional(possibleAddressesMapping)
    )
    (InprogressForces.apply)
    (InprogressForces.unapply)
    verifying (validateForces)
  )
  
  lazy val validateForces = Constraint[InprogressForces]("validateForces") { app =>
    val validateWaysToVote = app.waysToVote match {
      case None => Some(keys.waysToVote)
      case Some(waysToVote) => { 
        waysToVote  match {
          case WaysToVote(WaysToVoteType.InPerson) => None
          case _ => {
            app.postalOrProxyVote match {
              case None => Some(keys.waysToVote)
              case Some(vote) => None
            }
          }
        }
      }
    }
    val errorKeys = List(
      if (app.statement.isDefined) None else Some(keys.statement),
      if (app.address.isDefined) None else Some(keys.address),
      if (app.nationality.isDefined) None else Some(keys.nationality),
      if (app.dob.isDefined) None else Some(keys.dob),
      if (app.name.isDefined) None else Some(keys.name),
      if (app.nino.isDefined) None else Some(keys.nino),
      if (app.service.isDefined) None else Some(keys.service),
      if (app.rank.isDefined) None else Some(keys.rank),
      if (app.contactAddress.isDefined) None else Some(keys.contactAddress),
      if (app.openRegisterOptin.isDefined) None else Some(keys.openRegister),
      validateWaysToVote,
      if (app.contact.isDefined) None else Some(keys.contact)
    ).flatten
    if (errorKeys.size == 0) {
      Valid
    } else {
      Invalid ("Please complete this step", errorKeys:_*)
    }
  }
}
