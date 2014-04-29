package uk.gov.gds.ier.transaction.crown.previousAddress

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait PreviousAddressPostcodeMustache
  extends StepTemplate[InprogressCrown] {

    val title = "What was your previous UK address?"

    case class PostcodeModel (
        question: Question,
        postcode: Field
    )

  val mustache = MustacheTemplate("crown/previousAddressPostcode") {
    (form, post) =>
    implicit val progressForm = form
    val modelData = PostcodeModel(
      question = Question(
        postUrl = post.url,
        title = title,
        errorMessages = form.globalErrors.map(_.message)
      ),
      postcode = TextField(keys.previousAddress.postcode)
    )
    MustacheData(modelData, title)
  }
}

