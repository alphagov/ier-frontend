package uk.gov.gds.ier.transaction.crown.job

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.templates.Html
import uk.gov.gds.ier.model.{CrownStatement, Statement, InprogressCrown}
import play.api.mvc.Call
import uk.gov.gds.ier.mustache.StepMustache

trait JobMustache extends StepMustache {

  case class JobModel(
     question:Question,
     jobTitle: Field,
     govDepartment: Field)

  def transformFormStepToMustacheData(
     application: InprogressCrown,
     form:ErrorTransformForm[InprogressCrown],
     post: Call,
     back: Option[Call]): JobModel = {

    implicit val progressForm = form

    JobModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map (_.url).getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "6",
        title = if (displayPartnerSentence(application))
          "What is your partner's role?"
        else
          "What is your role?"
      ),
      jobTitle = TextField(
        key = keys.job.jobTitle
      ),
      govDepartment = TextField(
        key = keys.job.govDepartment
      )
    )
  }

  def jobMustache(
       application: InprogressCrown,
       form:ErrorTransformForm[InprogressCrown],
       post: Call,
       back: Option[Call]): Html = {

    val data = transformFormStepToMustacheData(application, form, post, back)
    val content = Mustache.render("crown/job", data)
    MainStepTemplate(content, data.question.title)
  }

  private def displayPartnerSentence (application:InprogressCrown): Boolean = {
    val statement = application.statement
    if (!statement.isDefined) {
      false
    }
    else if (statement.get.crownMember == Some(true) ||
             statement.get.britishCouncilMember == Some(true)) {
      false
    }
    else {
      true
    }
  }
}
