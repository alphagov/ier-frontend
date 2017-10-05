package uk.gov.gds.ier.transaction.overseas.nino

import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate


trait NinoMustache extends StepTemplate[InprogressOverseas] {

  val title = "www.gov.uk/register-to-vote - What is your National Insurance number?"

  val newQuestion = "What is your National Insurance number?"

  case class NinoModel(
      question:Question,
      nino: Field,
      noNinoReason: Field,
      emailField: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("overseas/nino") { (form, post) =>

    implicit val progressForm = form

    val emailAddress = form(keys.contact.email.detail).value

    NinoModel(
      question = Question(
        postUrl = post.url,
        errorMessages = form.globalErrors.map{ _.message },
        title = title,
        newQuestion = newQuestion
      ),
      nino = TextField(
        key = keys.nino.nino
      ),
      noNinoReason = TextField(
        key = keys.nino.noNinoReason
      ),
      emailField = TextField(
        key = keys.contact.email.detail,
        default = emailAddress
      )
    )
  }
}
