package uk.gov.gds.ier.transaction.overseas.nino

import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate


trait NinoMustache extends StepTemplate[InprogressOverseas] {

  val title = "What is your National Insurance number?"

  case class NinoModel(
      question:Question,
      nino: Field,
      noNinoReason: Field
  )

  val mustache = MustacheTemplate("overseas/nino") { (form, post, back) =>

    implicit val progressForm = form

    val data = NinoModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "",
        title = title
      ),
      nino = TextField(
        key = keys.nino.nino
      ),
      noNinoReason = TextField(
        key = keys.nino.noNinoReason
      )
    )
    MustacheData(data, title)
  }
}
