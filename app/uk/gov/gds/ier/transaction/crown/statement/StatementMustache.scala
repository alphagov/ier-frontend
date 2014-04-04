package uk.gov.gds.ier.transaction.crown.statement

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait StatementMustache extends StepMustache {

  case class StatementModel(
      question: Question,
      crown: Field,
      crownServant: Field,
      crownPartner: Field,
      council: Field,
      councilEmployee: Field,
      councilPartner: Field
  )

  def statementData(
      form: ErrorTransformForm[InprogressCrown],
      postEndpoint: Call,
      backEndpoint: Option[Call]) = {
    implicit val progressForm = form
    StatementModel(
      question = Question(
        postUrl = postEndpoint.url,
        backUrl = backEndpoint.map { _.url }.getOrElse(""),
        errorMessages = form.globalErrors.map { _.message },
        number = "1",
        title = "Which of these statements applies to you?"
      ),
      crown = Field(
        id = "crown" + keys.statement.key,
        classes = if (form(keys.statement).hasErrors) "invalid" else ""
      ),
      crownServant = CheckboxField(keys.statement.crownServant, "true"),
      crownPartner = CheckboxField(keys.statement.crownPartner, "true"),
      council = Field(
        id = "council" + keys.statement.key,
        classes = if (form(keys.statement).hasErrors) "invalid" else ""
      ),
      councilEmployee = CheckboxField(keys.statement.councilEmployee, "true"),
      councilPartner = CheckboxField(keys.statement.councilPartner, "true")
    )
  }

  def statementMustache(
      form:ErrorTransformForm[InprogressCrown],
      postEndpoint: Call,
      backEndpoint: Option[Call]): Html = {
    val data = statementData(form, postEndpoint, backEndpoint)
    val content = Mustache.render("crown/statement", data)
    MainStepTemplate(content, data.question.title)
  }
}
