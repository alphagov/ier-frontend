package uk.gov.gds.ier.transaction.forces.previousAddress

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.forces.InprogressForces

trait PreviousAddressPostcodeMustache
  extends StepTemplate[InprogressForces] {

    val title = "What was your previous UK address?"
    val questionNumber = "3"

    case class PostcodeModel (
        question: Question,
        postcode: Field
    )

  val mustache = MustacheTemplate("forces/previousAddressPostcode") {
    (form, post, back) =>
    implicit val progressForm = form
    val modelData = PostcodeModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map { _.url }.getOrElse(""),
        number = questionNumber,
        title = title,
        errorMessages = form.globalErrors.map(_.message)
      ),
      postcode = TextField(keys.previousAddress.postcode)
    )
    MustacheData(modelData, title)
  }
}

