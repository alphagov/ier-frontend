package uk.gov.gds.ier.validation

import play.api.data.Form
import play.api.data.Forms._
import uk.gov.gds.ier.model.InprogressApplication
import uk.gov.gds.ier.model.Address
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

  val inprogressForm = ErrorTransformForm(
    mapping(
      keys.name.key -> optional(nameMapping).verifying("Please complete this step", _.isDefined),
      keys.previousName.key -> optional(previousNameMapping).verifying("Please complete this step", _.isDefined),
      keys.dob.key -> optional(dobAndReasonMapping).verifying("Please complete this step", _.isDefined),
      keys.nationality.key -> optional(nationalityMapping).verifying("Please complete this step", _.isDefined),
      keys.nino.key -> optional(ninoMapping).verifying("Please complete this step", _.isDefined),
      keys.address.key -> optional(partialAddressMapping).verifying("Please complete this step", _.isDefined),
      keys.previousAddress.key -> optional(previousAddressMapping).verifying("Please complete this step", _.isDefined),
      keys.otherAddress.key -> optional(otherAddressMapping).verifying("Please complete this step", _.isDefined),
      keys.openRegister.key -> optional(optInMapping).verifying("Please complete this step", _.isDefined),
      keys.postalVote.key -> optional(optInMapping).verifying("Please complete this step", _.isDefined),
      keys.contact.key -> optional(contactMapping).verifying("Please complete this step", _.isDefined),
      keys.possibleAddresses.key -> optional(possibleAddressMapping),
      keys.country.key -> optional(countryMapping)
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
