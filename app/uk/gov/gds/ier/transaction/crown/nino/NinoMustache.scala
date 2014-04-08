package uk.gov.gds.ier.transaction.crown.nino

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.crown.InprogressCrown


trait NinoMustache extends StepTemplate[InprogressCrown] {

  case class NinoModel (
      question:Question,
      nino: Field,
      noNinoReason: Field,
      noNinoReasonShowFlag: Text
  )

  val mustache = MustacheTemplate("crown/nino") { (form, postEndpoint, backEndpoint) =>

    implicit val progressForm = form

    val title = "What is your National Insurance number?"

    val data = NinoModel(
      question = Question(
        postUrl = postEndpoint.url,
        backUrl = backEndpoint.map(_.url).getOrElse(""),
        errorMessages = form.globalErrors.map(_.message),
        number = "7",
        title = title
      ),
      nino = TextField(
        key = keys.nino.nino
      ),
      noNinoReason = TextField(
        key = keys.nino.noNinoReason
      ),
      noNinoReasonShowFlag = Text (
        value = progressForm(keys.nino.noNinoReason).value.fold("")(noNinoReason => "-open")
      )
    )
    MustacheData(data, title)
  }
}
