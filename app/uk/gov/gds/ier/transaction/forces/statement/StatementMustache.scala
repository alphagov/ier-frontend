package uk.gov.gds.ier.transaction.forces.statement

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.step.StepTemplate

trait StatementMustache extends StepTemplate[InprogressForces] {

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

  val mustache = MustacheTemplate("forces/statement") { (form, postUrl, backUrl) =>
    implicit val progressForm = form

    val data = transformFormStepToMustacheData(form, postUrl, backUrl)
    val title = data.question.title

    MustacheData(data, title)
  }
}
