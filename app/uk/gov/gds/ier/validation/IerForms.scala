package uk.gov.gds.ier.validation

import play.api.data.Form
import play.api.data.Forms._
import uk.gov.gds.ier.model.InprogressApplication
import uk.gov.gds.ier.serialiser.{JsonSerialiser, WithSerialiser}
import com.google.inject.{Inject, Singleton}

trait IerForms extends FormMappings {
  self: WithSerialiser =>

  val dobFormat = "yyyy-MM-dd"
  val timeFormat = "yyyy-MM-dd HH:mm:ss"
  val postcodeForm = Form(
    single(
      "postcode" -> nonEmptyText.verifying(PostcodeValidator.isValid(_))
    )
  )
  val completePostcodeForm = Form(
    single(
      keys.address.postcode.key -> nonEmptyText
    )
  )

  val nationalityForm = Form(
    mapping(keys.nationality.key -> nationalityMapping)
      (nationality => InprogressApplication(nationality = Some(nationality)))
      (inprogressApplication => inprogressApplication.nationality)
  )
  val dateOfBirthForm = Form(
    mapping(keys.dob.key -> optional(dobMapping).verifying("Please enter your date of birth", _.isDefined))
      (dob => InprogressApplication(dob = dob))
      (inprogress => Some(inprogress.dob))
  )
  val ninoForm = Form(
    mapping(keys.nino.key -> optional(ninoMapping.verifying(
      "Please enter your National Insurance number", nino => nino.nino.isDefined || nino.noNinoReason.isDefined)
    ).verifying("Please enter your National Insurance number", nino => nino.isDefined))
      (nino => InprogressApplication(nino = nino))
      (inprogress => Some(inprogress.nino))
  )
  val addressForm = Form(
    mapping(
      keys.address.key -> optional(addressMapping).verifying("Please answer this question", _.isDefined),
      keys.possibleAddresses.key -> optional(possibleAddressMapping)
    ) ((address, possibleAddresses) => InprogressApplication(address = address, possibleAddresses = possibleAddresses))
      (inprogress => Some(inprogress.address, inprogress.possibleAddresses))
  )
  val previousAddressForm = Form(
    mapping(
      keys.previousAddress.key -> optional(previousAddressMapping).verifying("Please answer this question", previousAddress => previousAddress.isDefined),
      keys.possibleAddresses.key -> optional(possibleAddressMapping)
    ) ((prevAddress, possibleAddresses) => InprogressApplication(previousAddress = prevAddress, possibleAddresses = possibleAddresses))
      (inprogress => Some(inprogress.previousAddress, inprogress.possibleAddresses))
  )
  val otherAddressForm = Form(
    mapping(keys.otherAddress.key -> optional(otherAddressMapping).verifying("Please answer this question", otherAddress => otherAddress.isDefined))
      (otherAddress => InprogressApplication(otherAddress = otherAddress))
      (inprogress => Some(inprogress.otherAddress))
  )
  val openRegisterForm = Form(
    mapping(keys.openRegister.key -> optional(optInMapping))
      (openRegister => InprogressApplication(openRegisterOptin = openRegister.orElse(Some(true))))
      (inprogress => Some(inprogress.openRegisterOptin))
  )
  val postalVoteForm = Form(
    mapping(keys.postalVote.key -> optional(optInMapping).verifying("Please answer this question", postalVote => postalVote.isDefined))
      (postalVote => InprogressApplication(postalVoteOptin = postalVote))
      (inprogress => Some(inprogress.postalVoteOptin))
  )
  val contactForm = Form(
    mapping(keys.contact.key -> optional(contactMapping).verifying("Please answer this question", _.isDefined))
    (contact => InprogressApplication(contact = contact))
      (inprogress => Some(inprogress.contact))
  )

  val inprogressForm = Form(
    mapping(
      keys.name.key -> optional(nameMapping).verifying("Please complete this step", _.isDefined),
      keys.previousName.key -> optional(previousNameMapping).verifying("Please complete this step", _.isDefined),
      keys.dob.key -> optional(dobMapping).verifying("Please complete this step", _.isDefined),
      keys.nationality.key -> optional(nationalityMapping).verifying("Please complete this step", _.isDefined),
      keys.nino.key -> optional(ninoMapping).verifying("Please complete this step", _.isDefined),
      keys.address.key -> optional(addressMapping).verifying("Please complete this step", _.isDefined),
      keys.previousAddress.key -> optional(previousAddressMapping).verifying("Please complete this step", _.isDefined),
      keys.otherAddress.key -> optional(otherAddressMapping).verifying("Please complete this step", _.isDefined),
      keys.openRegister.key -> optional(optInMapping).verifying("Please complete this step", _.isDefined),
      keys.postalVote.key -> optional(optInMapping).verifying("Please complete this step", _.isDefined),
      keys.contact.key -> optional(contactMapping).verifying("Please complete this step", _.isDefined),
      keys.possibleAddresses.key -> optional(possibleAddressMapping)
    ) (InprogressApplication.apply) (InprogressApplication.unapply)
  )

  implicit class FormWithErrorsAsMap[A](form: Form[A]) {
    def errorsAsMap = {
      form.errors.groupBy(_.key).mapValues {
        errors =>
          errors.map(e => play.api.i18n.Messages(e.message, e.args: _*))
      }
    }
    def simpleErrors: Map[String, String] = {
      form.errors.foldLeft(Map.empty[String, String]){
        (map, error) => map ++ Map(error.key -> play.api.i18n.Messages(error.message, error.args: _*))
      }
    }
  }

  object InProgress {
    def apply(application:InprogressApplication):InProgressForm = {
      InProgressForm(inprogressForm.fill(application))
    }
  }
}
