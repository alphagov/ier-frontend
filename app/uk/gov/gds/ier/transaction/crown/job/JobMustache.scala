package uk.gov.gds.ier.transaction.crown.job

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.{CrownStatement, Statement}
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait JobMustache extends StepTemplate[InprogressCrown] {

  case class JobModel(
     question:Question,
     jobTitle: Field,
     payrollNumber: Field,
     govDepartment: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("crown/job") { (form, post, application) =>

    implicit val progressForm = form

    val title = if (application.displayPartner) {
      "www.gov.uk/register-to-vote - What is your partner's role?"
    } else {
      "www.gov.uk/register-to-vote - What is your role?"
    }

    val newQuestion = if (application.displayPartner) {
      "What is your partner's role?"
    } else {
      "What is your role?"
    }

    JobModel(
      question = Question(
        postUrl = post.url,
        errorMessages = form.globalErrors.map{ _.message },
        title = title,
        newQuestion = newQuestion
      ),
      jobTitle = TextField(
        key = keys.job.jobTitle
      ),
      payrollNumber = TextField(
        key = keys.job.payrollNumber
      ),
      govDepartment = TextField(
        key = keys.job.govDepartment
      )
    )
  }
}
