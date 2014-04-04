package uk.gov.gds.ier.transaction.forces.rank

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.templates.Html
import uk.gov.gds.ier.model.{Statement}
import play.api.mvc.Call
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.transaction.forces.InprogressForces

trait RankMustache extends StepMustache {

  case class RankModel(
     question:Question,
     serviceNumber: Field,
     rank: Field)

  def transformFormStepToMustacheData(
     application: InprogressForces,
     form:ErrorTransformForm[InprogressForces],
     post: Call,
     back: Option[Call]): RankModel = {

    implicit val progressForm = form

    RankModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map (_.url).getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "9",
        title = if (displayPartnerSentence(application))
          "What is your partner's service number?"
        else
          "What is your service number?"
      ),
      serviceNumber = TextField(
        key = keys.rank.serviceNumber
      ),
      rank = TextField(
        key = keys.rank.rank
      )
    )
  }

  def rankMustache(
       application: InprogressForces,
       form:ErrorTransformForm[InprogressForces],
       post: Call,
       back: Option[Call]): Html = {

    val data = transformFormStepToMustacheData(application, form, post, back)
    val content = Mustache.render("forces/rank", data)
    MainStepTemplate(content, data.question.title)
  }

  private def displayPartnerSentence (application:InprogressForces): Boolean = {
    application.statement match {
      case Some(Statement(Some(false), Some(true))) => true
      case Some(Statement(None, Some(true))) => true
      case _ => false
    }
  }
}
