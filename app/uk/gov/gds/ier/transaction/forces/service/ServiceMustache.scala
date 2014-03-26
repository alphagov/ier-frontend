package uk.gov.gds.ier.transaction.forces.service

import uk.gov.gds.ier.validation.{InProgressForm, ErrorTransformForm}
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.model.{Statement, InprogressForces}

trait ServiceMustache extends StepMustache {

  case class ServiceModel (
      question:Question,
      serviceFieldSet: FieldSet,
      royalNavy: Field,
      britishArmy: Field,
      royalAirForce: Field,
      regiment: Field,
      regimentShowFlag: Text
  )

  def transformFormStepToMustacheData(
      application: InprogressForces,
      form: ErrorTransformForm[InprogressForces],
      postEndpoint: Call,
      backEndpoint:Option[Call]) : ServiceModel = {

    implicit val progressForm = form

    def makeRadio(serviceName:String) = {
      Field(
        id = keys.service.serviceName.asId(serviceName),
        name = keys.service.serviceName.key,
        attributes = if (progressForm(keys.service.serviceName.key).value == Some(serviceName))
          "checked=\"checked\"" else ""
      )
    }

    ServiceModel(
      question = Question(
        postUrl = postEndpoint.url,
        backUrl = backEndpoint.fold("")(_.url),
        errorMessages = form.globalErrors.map{ _.message },
        number = "8",
        title = if (displayPartnerSentence(application))
                  "Which of the services is your partner in?"
                else
                  "Which of the services are you in?"
      ),
      serviceFieldSet = FieldSet(
        classes = if (progressForm(keys.service.key).hasErrors) "invalid" else ""
      ),
      royalNavy = makeRadio("Royal Navy"),
      britishArmy = makeRadio("British Army"),
      royalAirForce = makeRadio("Royal Air Force"),
      regiment = TextField(
        key = keys.service.regiment
      ),
      regimentShowFlag = Text (
        value = progressForm(keys.service.regiment.key).value.fold("")(_ => "-open")
      )
    )
  }

  def serviceMustache(
      application: InprogressForces,
      form:ErrorTransformForm[InprogressForces],
      postEndpoint: Call,
      backEndpoint: Option[Call]): Html = {

    val data = transformFormStepToMustacheData(application, form, postEndpoint, backEndpoint)
    val content = Mustache.render("forces/service", data)
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
