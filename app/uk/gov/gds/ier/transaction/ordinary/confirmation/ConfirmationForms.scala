package uk.gov.gds.ier.transaction.ordinary.confirmation

import uk.gov.gds.ier.validation.{OrdinaryMappings, ErrorTransformForm}
import play.api.data.Forms._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait ConfirmationForms extends OrdinaryMappings {
  self: WithSerialiser =>

  val confirmationForm = ErrorTransformForm(
    mapping(
      keys.name.key -> optional(nameMapping).verifying("Please complete this step", _.isDefined),
      keys.previousName.key -> optional(previousNameMapping).verifying("Please complete this step", _.isDefined),
      keys.dob.key -> optional(dobAndReasonMapping).verifying("Please complete this step", _.isDefined),
      keys.nationality.key -> optional(nationalityMapping).verifying("Please complete this step", _.isDefined),
      keys.nino.key -> optional(ninoMapping).verifying("Please complete this step", _.isDefined),
      keys.address.key -> optional(partialAddressMapping).verifying("Please complete this step", _.isDefined),
      keys.previousAddress.key ->
        optional(PartialPreviousAddress.mapping.verifying(previousAddressRequiredIfMoved))
        .verifying("Please complete this step", _.isDefined),
      keys.otherAddress.key -> optional(otherAddressMapping).verifying("Please complete this step", _.isDefined),
      keys.openRegister.key -> optional(optInMapping).verifying("Please complete this step", _.isDefined),
      keys.postalVote.key -> optional(PostalVote.mapping).verifying("Please complete this step", _.isDefined),
      keys.contact.key -> optional(Contact.mapping).verifying("Please complete this step", _.isDefined),
      keys.possibleAddresses.key -> optional(possibleAddressesMapping),
      keys.country.key -> optional(countryMapping)

    ) (InprogressOrdinary.apply) (InprogressOrdinary.unapply)
  )

}
