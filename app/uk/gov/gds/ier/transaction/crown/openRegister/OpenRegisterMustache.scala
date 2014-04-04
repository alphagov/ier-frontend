package uk.gov.gds.ier.transaction.crown.openRegister

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait OpenRegisterMustache extends StepMustache {

  case class OpenRegisterModel(question:Question,
                               openRegister: Field)

  def transformFormStepToMustacheData(
      form: ErrorTransformForm[InprogressCrown],
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
      form:ErrorTransformForm[InprogressCrown],
      postEndpoint: Call,
      backEndpoint: Option[Call]): Html = {
    val data = transformFormStepToMustacheData(form, postEndpoint, backEndpoint)
    val content = Mustache.render("crown/openRegister", data)
    MainStepTemplate(content, data.question.title)
  }
}
