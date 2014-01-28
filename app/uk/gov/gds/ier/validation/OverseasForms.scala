package uk.gov.gds.ier.validation

import uk.gov.gds.ier.transaction.overseas.previouslyRegistered.PreviouslyRegisteredForms
import uk.gov.gds.ier.transaction.overseas.dateLeftUk.DateLeftUkForms

trait OverseasForms
  extends FormKeys
  with ErrorMessages
  with PreviouslyRegisteredForms
  with DateLeftUkForms {

}
