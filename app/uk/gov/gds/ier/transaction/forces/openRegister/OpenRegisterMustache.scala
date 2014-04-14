package uk.gov.gds.ier.transaction.forces.openRegister

import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.step.StepTemplate

trait OpenRegisterMustache extends StepTemplate[InprogressForces] {

  case class OpenRegisterModel(
    question:Question,
    openRegister: Field)

  val mustache = MustacheTemplate("forces/openRegister") { (form, post, back) =>
    implicit val progressForm = form
    val title = "Do you want to include your name and address on the open register?"
    val data = OpenRegisterModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "11",
        title = title
      ),
      openRegister = CheckboxField (
        key = keys.openRegister.optIn,
        value = "false"
      )
    )
    MustacheData(data, title)
  }
}
