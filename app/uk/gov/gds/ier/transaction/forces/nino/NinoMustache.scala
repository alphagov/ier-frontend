package uk.gov.gds.ier.transaction.forces.nino

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressForces
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache


trait NinoMustache extends StepMustache {

  case class NinoModel (
      question:Question,
      nino: Field,
      noNinoReason: Field,
      noNinoReasonShowFlag: Text
  )

  def ninoMustache(
      form: ErrorTransformForm[InprogressForces],
      postEndpoint: Call,
      backEndpoint:Option[Call]) : Html = {

    implicit val progressForm = form

    val data = NinoModel(
      question = Question(
        postUrl = postEndpoint.url,
        backUrl = backEndpoint.map(_.url).getOrElse(""),
        errorMessages = form.globalErrors.map(_.message),
        number = "7",
        title = "What is your National Insurance number?"
      ),
      nino = TextField(
        key = keys.nino.nino
      ),
      noNinoReason = TextField(
        key = keys.nino.noNinoReason
      ),
      noNinoReasonShowFlag = Text (
        value = progressForm(keys.nino.noNinoReason.key).value.fold("")(noNinoReason => "-open")
      )
    )
    val content = Mustache.render("forces/nino", data)
    MainStepTemplate(content, data.question.title)
  }
}
