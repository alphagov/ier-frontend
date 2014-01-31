package uk.gov.gds.ier.validation

import uk.gov.gds.ier.transaction.overseas.previouslyRegistered.PreviouslyRegisteredForms
import uk.gov.gds.ier.transaction.overseas.dateLeftUk.DateLeftUkForms
import uk.gov.gds.ier.transaction.overseas.dateOfBirth.DateOfBirthForms
import uk.gov.gds.ier.transaction.overseas.lastRegisteredToVote.LastRegisteredToVoteForms
import uk.gov.gds.ier.transaction.overseas.address.AddressForms
import uk.gov.gds.ier.transaction.overseas.nino.NinoForms

trait OverseasForms
  extends FormKeys
  with ErrorMessages
  with PreviouslyRegisteredForms
  with DateLeftUkForms
  with DateOfBirthForms
  with AddressForms 
  with LastRegisteredToVoteForms
  with NinoForms {
}
