package uk.gov.gds.ier.validation

import uk.gov.gds.ier.transaction.overseas.previouslyRegistered.PreviouslyRegisteredForms
import uk.gov.gds.ier.transaction.overseas.name.NameForms

trait OverseasForms
  extends FormKeys
  with ErrorMessages
  with NameForms
  with PreviouslyRegisteredForms {

}
