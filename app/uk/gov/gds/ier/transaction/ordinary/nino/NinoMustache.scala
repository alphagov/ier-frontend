package uk.gov.gds.ier.transaction.ordinary.nino

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary


trait NinoMustache extends StepTemplate[InprogressOrdinary] {

  case class NinoModel (
      question:Question,
      nino: Field,
      noNinoReason: Field,
      noNinoReasonShowFlag: Text
  )

  val mustache = MustacheTemplate("ordinary/nino") {
    (form, postEndpoint) =>
    
    implicit val progressForm = form

    val title = "What is your National Insurance number?"

    val data = NinoModel(
      question = Question(
        postUrl = postEndpoint.url,
        errorMessages = form.globalErrors.map{ _.message },
        number = "5 of 11",
        title = title
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

    MustacheData(data, title)
  }
}
