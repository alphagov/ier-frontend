package uk.gov.gds.ier.transaction.overseas.parentsAddress

import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait ParentsAddressLookupMustache extends StepTemplate[InprogressOverseas] {

  val title = "www.gov.uk/register-to-vote - What was your parent or guardian's last UK address?"
  val newQuestion = "What was your parent or guardian's last UK address?"
  val questionNumber = ""

  case class LookupModel (
      question: Question,
      postcode: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("overseas/parentsAddressLookup") { (form, post) =>

    implicit val progressForm = form

    LookupModel(
      question = Question(
        postUrl = post.url,
        title = title,
        newQuestion = newQuestion,
        errorMessages = form.globalErrors.map(_.message)
      ),
      postcode = Field(
        id = keys.parentsAddress.postcode.asId(),
        name = keys.parentsAddress.postcode.key,
        value = form(keys.parentsAddress.postcode).value.getOrElse(""),
        classes = if (form(keys.parentsAddress.postcode).hasErrors) {
          "invalid"
        } else {
          ""
        }
      )
    )
  }
}
