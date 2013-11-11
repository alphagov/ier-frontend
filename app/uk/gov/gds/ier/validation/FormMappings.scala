package uk.gov.gds.ier.validation

import play.api.data.Forms._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.validation
import uk.gov.gds.ier.validation.DateValidator._
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.data.validation.{Invalid, Valid, Constraint}
import play.api.data.Form
import uk.gov.gds.ier.step.nationality.NationalityForms
import uk.gov.gds.ier.step.name.NameForms
import uk.gov.gds.ier.step.previousName.PreviousNameForms
import uk.gov.gds.ier.step.dateOfBirth.DateOfBirthForms
import uk.gov.gds.ier.step.nino.NinoForms
import uk.gov.gds.ier.step.address.AddressForms
import uk.gov.gds.ier.step.previousAddress.PreviousAddressForms
import uk.gov.gds.ier.step.otherAddress.OtherAddressForms
import uk.gov.gds.ier.step.openRegister.OpenRegisterForms
import uk.gov.gds.ier.step.postalVote.PostalVoteForms
import uk.gov.gds.ier.step.contact.ContactForms

trait FormMappings 
  extends Constraints 
  with FormKeys 
  with ErrorMessages 
  with NinoForms
  with NationalityForms 
  with NameForms 
  with AddressForms
  with OtherAddressForms
  with PreviousAddressForms
  with PreviousNameForms
  with DateOfBirthForms
  with OpenRegisterForms 
  with PostalVoteForms 
  with ContactForms {
    self: WithSerialiser =>

  val optInMapping = single(
    keys.optIn.key -> boolean
  )

}
