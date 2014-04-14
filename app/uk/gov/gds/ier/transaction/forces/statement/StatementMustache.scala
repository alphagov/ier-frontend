package uk.gov.gds.ier.transaction.forces.statement

import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.step.StepTemplate

trait StatementMustache extends StepTemplate[InprogressForces] {

  case class StatementModel (
      question:Question,
      statementFieldSet: FieldSet,
      statementMemberForcesCheckbox: Field,
      statementPartnerForcesCheckbox: Field
  )

  val mustache = MustacheTemplate("forces/statement") { (form, postEndpoint, backEndpoint) =>
    implicit val progressForm = form

    val title = "Which of these statements applies to you?"
    val data = StatementModel(
      question = Question(
        postUrl = postEndpoint.url,
        backUrl = backEndpoint.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "1",
        title = title,
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

    MustacheData(data, title)
  }
}
