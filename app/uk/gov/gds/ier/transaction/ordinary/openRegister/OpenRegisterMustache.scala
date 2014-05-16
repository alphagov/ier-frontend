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
  ) extends MustacheData

  val mustache = MustacheTemplate("ordinary/openRegister") {
    (form, postEndpoint) =>

    implicit val progressForm = form

    OpenRegisterModel(
      question = Question(
        postUrl = postEndpoint.url,
        errorMessages = Messages.translatedGlobalErrors(form),
        number = "9",
        title = Messages("ordinary_openRegister_title")
      ),
      openRegister = CheckboxField (
        key = keys.openRegister.optIn,
        value = "false"
      )
    )
  }
}

