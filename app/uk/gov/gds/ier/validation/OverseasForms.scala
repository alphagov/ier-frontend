package uk.gov.gds.ier.validation

import uk.gov.gds.ier.transaction.overseas.previouslyRegistered.PreviouslyRegisteredForms
import uk.gov.gds.ier.transaction.overseas.dateLeftUk.DateLeftUkForms
import uk.gov.gds.ier.transaction.overseas.dateOfBirth.DateOfBirthForms
import uk.gov.gds.ier.transaction.overseas.lastRegisteredToVote.LastRegisteredToVoteForms
import uk.gov.gds.ier.transaction.overseas.nino.NinoForms
import uk.gov.gds.ier.transaction.overseas.contact.ContactForms
import uk.gov.gds.ier.serialiser.WithSerialiser

trait OverseasForms
  extends FormKeys
  with WithSerialiser
  with ErrorMessages
  with PreviouslyRegisteredForms
  with DateLeftUkForms
  with DateOfBirthForms
  with LastRegisteredToVoteForms
  with NinoForms
  with ContactForms {

}
