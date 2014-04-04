package uk.gov.gds.ier.transaction.ordinary.nino

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary


trait NinoMustache extends StepMustache {

  case class NinoModel (
      question:Question,
      nino: Field,
      noNinoReason: Field,
      noNinoReasonShowFlag: Text
  )

  def ninoMustache(
      form: ErrorTransformForm[InprogressOrdinary],
      postEndpoint: Call,
      backEndpoint:Option[Call]) : Html = {

    implicit val progressForm = form

    val data = NinoModel(
      question = Question(
        postUrl = postEndpoint.url,
        backUrl = backEndpoint.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "5 of 11",
        title = "What is your National Insurance number?"
      ),
      nino = TextField(
        key = keys.nino.nino
      ),
      noNinoReason = TextField(
        key = keys.nino.noNinoReason
      ),
      noNinoReasonShowFlag = Text (
        value = progressForm(keys.nino.noNinoReason).value.map(noNinoReason => "-open").getOrElse("")
      )
    )
    val content = Mustache.render("ordinary/nino", data)
    MainStepTemplate(content, data.question.title)
  }
}
