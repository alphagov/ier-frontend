package uk.gov.gds.ier.transaction.ordinary.openRegister

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait OpenRegisterMustache extends StepTemplate[InprogressOrdinary] {

  case class OpenRegisterModel(
      question:Question,
      openRegister: Field
  )

  val mustache = MustacheTemplate("ordinary/openRegister") {
    (form, postEndpoint, backEndpoint) =>

    implicit val progressForm = form

    val title = "Do you want to include your name and address on the open register?"

    val data = OpenRegisterModel(
      question = Question(
        postUrl = postEndpoint.url,
        backUrl = backEndpoint.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "9",
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

