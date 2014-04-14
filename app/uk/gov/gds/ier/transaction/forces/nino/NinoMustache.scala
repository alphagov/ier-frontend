package uk.gov.gds.ier.transaction.forces.nino

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.forces.InprogressForces


trait NinoMustache extends StepTemplate[InprogressForces] {

  case class NinoModel (
      question:Question,
      nino: Field,
      noNinoReason: Field,
      noNinoReasonShowFlag: Text
  )

  val mustache = MustacheTemplate("forces/nino") { (form, postEndpoint, backEndpoint) =>

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
