package uk.gov.gds.ier.transaction.ordinary.openRegister

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressOrdinary
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache

trait OpenRegisterMustache extends StepMustache {

  case class OpenRegisterModel(question:Question,
                               openRegister: Field)

  def transformFormStepToMustacheData(
      form: ErrorTransformForm[InprogressOrdinary],
      postEndpoint: Call,
      backEndpoint: Option[Call]) : OpenRegisterModel = {
    implicit val progressForm = form
    OpenRegisterModel(
      question = Question(
        postUrl = postEndpoint.url,
        backUrl = backEndpoint.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "9",
        title = "Do you want to include your name and address on the open register?"
      ),
      openRegister = CheckboxField (
        key = keys.openRegister.optIn,
        value = "false"
      )
    )
  }

  def openRegisterMustache(
      form:ErrorTransformForm[InprogressOrdinary],
      postEndpoint: Call,
      backEndpoint: Option[Call]): Html = {
    val data = transformFormStepToMustacheData(form, postEndpoint, backEndpoint)
    val content = Mustache.render("ordinary/openRegister", data)
    MainStepTemplate(content, data.question.title)
  }
}
