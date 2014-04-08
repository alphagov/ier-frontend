package uk.gov.gds.ier.transaction.crown.statement

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait StatementMustache extends StepTemplate[InprogressCrown] {

  case class StatementModel(
      question: Question,
      crown: Field,
      crownServant: Field,
      crownPartner: Field,
      council: Field,
      councilEmployee: Field,
      councilPartner: Field
  )

  val title = "Which of these statements applies to you?"

  val mustache = MustacheTemplate("crown/statement") { (form, post, back) =>
    implicit val progressForm = form
    
    val data = StatementModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map { _.url }.getOrElse(""),
        errorMessages = form.globalErrors.map { _.message },
        number = "1",
        title = title
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

    MustacheData(data, title)
  }
}

