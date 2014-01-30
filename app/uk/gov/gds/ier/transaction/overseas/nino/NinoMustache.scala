package uk.gov.gds.ier.transaction.overseas.nino

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.model.InprogressOverseas


trait NinoMustache extends StepMustache {

  case class NinoModel(question:Question,
                       nino: Field,
                       noNinoReason: Field)

  def ninoMustache(form: ErrorTransformForm[InprogressOverseas], postEndpoint: Call, backEndpoint:Option[Call]) : Html = {
    implicit val progressForm = form

    val data = NinoModel(
      question = Question(
        postUrl = postEndpoint.url,
        backUrl = backEndpoint.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "7",
        title = "What is your National Insurance number?"
      ),
      nino = TextField(
        key = keys.nino.nino)
      ,
      noNinoReason = TextField(
        key = keys.nino.noNinoReason)
    )
    val content = Mustache.render("overseas/nino", data)
    MainStepTemplate(content, data.question.title)
  }
}
