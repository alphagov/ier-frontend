package uk.gov.gds.ier.transaction.forces.statement

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.transaction.forces.InprogressForces

trait StatementMustache extends StepMustache {

  case class StatementModel (
      question:Question,
      statementFieldSet: FieldSet,
      statementMemberForcesCheckbox: Field,
      statementPartnerForcesCheckbox: Field
  )

  def transformFormStepToMustacheData(
      form: ErrorTransformForm[InprogressForces],
      postEndpoint: Call,
      backEndpoint:Option[Call]) : StatementModel = {

    implicit val progressForm = form
    StatementModel(
      question = Question(
        postUrl = postEndpoint.url,
        backUrl = backEndpoint.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "1",
        title = "Which of these statements applies to you?",
        showBackUrl = false
      ),
      statementFieldSet = FieldSet(
        classes = if (progressForm(keys.contact).hasErrors) "invalid" else ""
      ),
      statementMemberForcesCheckbox = CheckboxField(
        key = keys.statement.forcesMember, value = "true"
      ),
      statementPartnerForcesCheckbox = CheckboxField(
        key = keys.statement.partnerForcesMember, value = "true"
      )
    )
  }

  def statementMustache(
      form:ErrorTransformForm[InprogressForces],
      postEndpoint: Call,
      backEndpoint: Option[Call]): Html = {

    val data = transformFormStepToMustacheData(form, postEndpoint, backEndpoint)
    val content = Mustache.render("forces/statement", data)
    MainStepTemplate(content, data.question.title)
  }
}
