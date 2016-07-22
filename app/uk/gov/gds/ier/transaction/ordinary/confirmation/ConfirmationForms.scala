package uk.gov.gds.ier.transaction.ordinary.confirmation

import uk.gov.gds.ier.validation._
import play.api.data.Forms._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import play.api.data.validation.{Invalid, Valid, Constraint, ValidationError}
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.service.WithScotlandService

trait ConfirmationForms extends OrdinaryMappings
with ConfirmationConstraints {
  self: WithSerialiser =>

  val confirmationForm = ErrorTransformForm(
    mapping(
      keys.name.key -> optional(Name.mapping),
      keys.previousName.key -> optional(PreviousName.mapping),
      keys.dob.key -> optional(dobAndReasonMapping),
      keys.nationality.key -> optional(PartialNationality.mapping),
      keys.nino.key -> optional(Nino.mapping),
      keys.address.key -> optional(partialAddressMapping),
      keys.previousAddress.key -> optional(PartialPreviousAddress.mapping.verifying(previousAddressRequiredIfMoved)),
      keys.otherAddress.key -> optional(OtherAddress.otherAddressMapping),
      keys.openRegister.key -> optional(optInMapping),
      keys.postalVote.key -> optional(PostalVote.mapping),
      keys.soleOccupancy.optIn.key -> optional(SoleOccupancyOption.mapping),
      keys.contact.key -> optional(Contact.mapping),
      keys.possibleAddresses.key -> optional(possibleAddressesMapping),
      keys.country.key -> optional(countryMapping),
      keys.sessionId.key -> optional(text)
    )
      (InprogressOrdinary.apply)
      (InprogressOrdinary.unapply)
      verifying(
        //Validate these object as being required on the confirmation page...
        nameStepRequired,
        previousNameStepRequired,
        dobStepRequired,
        nationalityStepRequired,
        addressStepRequired,
        previousAddressStepRequired,
        otherAddressStepRequired,
        postalVoteStepRequired,
        contactStepRequired,
        soleOccupancyRequiredIfNotScottish,
        //...and validate these with special isYoungScot validation conditionals...
        ninoIsYoungScot,
        openRegIsYoungScot
      )
  )

}
trait ConfirmationConstraints extends WithScotlandService {
  self: FormKeys
    with ErrorMessages =>

  val nameStepRequired = requireThis(keys.name) { _.name }
  val previousNameStepRequired = requireThis(keys.previousName) { _.previousName }
  val dobStepRequired = requireThis(keys.dob) { _.dob }
  val nationalityStepRequired = requireThis(keys.nationality) { _.nationality }
  val addressStepRequired = requireThis(keys.address) { _.address }
  val previousAddressStepRequired = requireThis(keys.previousAddress) { _.previousAddress }
  val otherAddressStepRequired = requireThis(keys.otherAddress) { _.otherAddress }
  val postalVoteStepRequired = requireThis(keys.postalVote) { _.postalVote }
  val contactStepRequired = requireThis(keys.contact) { _.contact }
  val soleOccupancyRequiredIfNotScottish = Constraint[InprogressOrdinary]("soleOccupancyRequired") {
    application =>
      if (scotlandService.isScot(application)) Valid
      else application.soleOccupancy match {
        case Some(_) => Valid
        case None => Invalid("ordinary_confirmation_error_completeThis", keys.soleOccupancy)
      }
  }

  //Given a key, validate that the object is completed or throw the standard error prompt onscreen
  def requireThis[T](key: Key)(extractT: InprogressOrdinary => Option[T]) = {
    Constraint[InprogressOrdinary](s"${key.name}required") {
      application =>
        extractT(application) match {
          case Some(_) => Valid
          case None => Invalid("ordinary_confirmation_error_completeThis", key)
        }
    }
  }

  //Validate that if isYoungScot=true, nino can be None else throw as invalid if incomplete
  val ninoIsYoungScot = Constraint[InprogressOrdinary]("ninoIsYoungScot") {
    application =>
      if(application.dob.exists(_.dob.isDefined)) {
        if ( scotlandService.isYoungScot(application)) {
          Valid
        }
        else {
          if(application.nino.isDefined) {
            Valid
          } else {
            Invalid(ValidationError("ordinary_confirmation_error_completeThis", keys.nino))
          }
        }
      }
      else {
        if(application.nino.isDefined) {
          Valid
        } else {
          Invalid(ValidationError("ordinary_confirmation_error_completeThis", keys.nino))
        }
      }
  }

  //Validate that if isYoungScot=true, openReg can be None else throw as invalid if incomplete
  val openRegIsYoungScot = Constraint[InprogressOrdinary]("openRegIsYoungScot") {
    application =>
      if(application.dob.exists(_.dob.isDefined)) {
        if ( scotlandService.isYoungScot(application) ) {
          Valid
        }
        else {
          if(application.openRegisterOptin.isDefined) {
            Valid
          } else {
            Invalid(ValidationError("ordinary_confirmation_error_completeThis", keys.openRegister))
          }
        }
      }
      else {
        if(application.openRegisterOptin.isDefined) {
          Valid
        } else {
          Invalid(ValidationError("ordinary_confirmation_error_completeThis", keys.openRegister))
        }
      }
  }

}
