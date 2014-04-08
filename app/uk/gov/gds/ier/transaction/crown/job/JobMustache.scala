package uk.gov.gds.ier.transaction.crown.job

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.{CrownStatement, Statement}
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait JobMustache extends StepTemplate[InprogressCrown] {

  case class JobModel(
     question:Question,
     jobTitle: Field,
     govDepartment: Field)

  val mustache = MustacheTemplate("crown/job") { (form, post, back, application) =>

    implicit val progressForm = form

    val title = if (application.displayPartner) {
      "What is your partner's role?"
    } else {
      "What is your role?"
    }

    val data = JobModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map (_.url).getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "6",
        title = title
      ),
      jobTitle = TextField(
        key = keys.job.jobTitle
      ),
      govDepartment = TextField(
        key = keys.job.govDepartment
      )
    )

    MustacheData(data, title)
  }
}
