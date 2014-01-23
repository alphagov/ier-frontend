package uk.gov.gds.ier.transaction.ordinary.nino

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressOrdinary
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache


trait NinoMustache extends StepMustache {

  case class NinoModel(question:Question,
                       nino: Field,
                       noNinoReason: Field)

  def ninoMustache(form: ErrorTransformForm[InprogressOrdinary], postEndpoint: Call, backEndpoint:Option[Call]) : Html = {

    val application:InprogressOrdinary = form.value.getOrElse(InprogressOrdinary())

    val data = NinoModel(
      question = Question(
        postUrl = postEndpoint.url,
        backUrl = backEndpoint.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "5 of 11",
        title = "What is your National Insurance number?"
      ),
      nino = Field(
        name = keys.nino.nino.key,
        id = keys.nino.nino.asId(),
        value = application.nino.map(ninoStepObject => ninoStepObject.nino.getOrElse("")).getOrElse(""),
        classes = if (form(keys.nino.nino.key).hasErrors) "invalid" else "",
        wrapperClasses = if (form(keys.nino.nino.key).hasErrors) "validation-wrapper-invalid" else ""
      ),
      noNinoReason = Field(
        name = keys.nino.noNinoReason.key,
        id = keys.nino.noNinoReason.asId(),
        value = application.nino.map(ninoStepObject => ninoStepObject.noNinoReason.getOrElse("")).getOrElse("")
      )
    )
    val content = Mustache.render("ordinary/nino", data)
    MainStepTemplate(content, "Register to Vote - What is your National Insurance number?")
  }
}
