package uk.gov.gds.ier.transaction.forces.service

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.{Statement}
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.step.StepTemplate

trait ServiceMustache extends StepTemplate[InprogressForces] {

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
        attributes = if (progressForm(keys.service.serviceName).value == Some(serviceName))
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
        classes = if (progressForm(keys.service).hasErrors) "invalid" else ""
      ),
      royalNavy = makeRadio("Royal Navy"),
      britishArmy = makeRadio("British Army"),
      royalAirForce = makeRadio("Royal Air Force"),
      regiment = TextField(
        key = keys.service.regiment
      ),
      regimentShowFlag = Text (
        value = progressForm(keys.service.regiment).value.fold("")(_ => "-open")
      )
    )
  }



  private def displayPartnerSentence (application:InprogressForces): Boolean = {
    application.statement match {
      case Some(Statement(Some(false), Some(true))) => true
      case Some(Statement(None, Some(true))) => true
      case _ => false
    }
  }



  val mustache = MustacheTemplate("forces/service") { (form, postUrl, backUrl, application) =>
    implicit val progressForm = form

    def makeRadio(serviceName:String) = {
      Field(
        id = keys.service.serviceName.asId(serviceName),
        name = keys.service.serviceName.key,
        attributes = if (progressForm(keys.service.serviceName).value == Some(serviceName))
          "checked=\"checked\"" else ""
      )
    }

    val title = if (displayPartnerSentence(application))
      "Which of the services is your partner in?"
    else
      "Which of the services are you in?"

    val data = ServiceModel(
      question = Question(
        postUrl = postUrl.url,
        backUrl = backUrl.fold("")(_.url),
        errorMessages = form.globalErrors.map{ _.message },
        number = "8",
        title = title
      ),
      serviceFieldSet = FieldSet(
        classes = if (progressForm(keys.service).hasErrors) "invalid" else ""
      ),
      royalNavy = makeRadio("Royal Navy"),
      britishArmy = makeRadio("British Army"),
      royalAirForce = makeRadio("Royal Air Force"),
      regiment = TextField(
        key = keys.service.regiment
      ),
      regimentShowFlag = Text (
        value = progressForm(keys.service.regiment).value.fold("")(_ => "-open")
      )
    )


    MustacheData(data, title)
  }

}
