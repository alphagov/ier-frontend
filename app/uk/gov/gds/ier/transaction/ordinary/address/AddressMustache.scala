package uk.gov.gds.ier.transaction.ordinary.address

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.{PossibleAddress}
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait AddressMustache extends StepTemplate[InprogressOrdinary] {
  self: WithSerialiser =>

  val title = "What is your address?"
  val questionNumber = "6 of 11"

  case class LookupModel (
      question: Question,
      postcode: Field
  )

  val mustache = MustacheTemplate("ordinary/addressLookup") {
    (form, post, back) =>

    val data = LookupModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map(_.url).getOrElse(""),
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

    MustacheData(data, title)
  }
}
