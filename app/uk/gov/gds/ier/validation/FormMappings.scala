package uk.gov.gds.ier.validation

import play.api.data.Forms._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.validation
import uk.gov.gds.ier.validation.DateValidator._
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.data.Form
import uk.gov.gds.ier.transaction.nationality.NationalityForms
import uk.gov.gds.ier.transaction.name.NameForms
import uk.gov.gds.ier.transaction.dateOfBirth.DateOfBirthForms
import uk.gov.gds.ier.transaction.nino.NinoForms
import uk.gov.gds.ier.transaction.address.AddressForms
import uk.gov.gds.ier.transaction.previousAddress.PreviousAddressForms
import uk.gov.gds.ier.transaction.otherAddress.OtherAddressForms
import uk.gov.gds.ier.transaction.openRegister.OpenRegisterForms
import uk.gov.gds.ier.transaction.postalVote.PostalVoteForms
import uk.gov.gds.ier.transaction.contact.ContactForms
import uk.gov.gds.ier.transaction.country.CountryForms

trait FormMappings 
  extends FormKeys 
  with ErrorMessages
  with NinoForms
  with NationalityForms 
  with NameForms 
  with AddressForms
  with OtherAddressForms
  with PreviousAddressForms
  with DateOfBirthForms
  with OpenRegisterForms 
  with PostalVoteForms 
  with ContactForms 
  with CountryForms {
    self: WithSerialiser =>

  val optInMapping = single(
    keys.optIn.key -> boolean
  )

}
