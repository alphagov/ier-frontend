package uk.gov.gds.ier.transaction.ordinary.address

import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.{PossibleAddress}
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait AddressMustache {
  self: WithSerialiser =>

  object AddressMustache extends StepMustache {

    val title = "What is your address?"
    val questionNumber = "6 of 11"

    case class LookupModel (
        question: Question,
        postcode: Field
    )

    def lookupData(
        form: ErrorTransformForm[InprogressOrdinary],
        backUrl: String,
        postUrl: String) = {
     LookupModel(
        question = Question(
          postUrl = postUrl,
          backUrl = backUrl,
          number = questionNumber,
          title = title,
          errorMessages = form.globalErrors.map(_.message)
        ),
        postcode = Field(
          id = keys.address.postcode.asId(),
          name = keys.address.postcode.key,
          value = form(keys.address.postcode).value.getOrElse(""),
          classes = if (form(keys.address.postcode).hasErrors) {
            "invalid"
          } else {
            ""
          }
        )
      )
    }

    def lookupPage(
        form: ErrorTransformForm[InprogressOrdinary],
        backUrl: String,
        postUrl: String) = {

      val content = Mustache.render(
        "ordinary/addressLookup",
        lookupData(form, backUrl, postUrl)
      )
      MainStepTemplate(content, title)
    }
  }
}
