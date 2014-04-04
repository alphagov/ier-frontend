package uk.gov.gds.ier.transaction.forces.name

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.templates.Html
import play.api.mvc.Call
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.transaction.forces.InprogressForces

trait NameMustache extends StepMustache {

  val pageTitle = "What is your full name?"

  case class NameModel(
    question: Question,
    firstName: Field,
    middleNames: Field,
    lastName: Field,
    hasPreviousName: FieldSet,
    hasPreviousNameTrue: Field,
    hasPreviousNameFalse: Field,
    previousFirstName: Field,
    previousMiddleNames: Field,
    previousLastName: Field)

    def transformFormStepToMustacheData(form: ErrorTransformForm[InprogressForces],
                                        postUrl: String,
                                        backUrl: Option[String]): NameModel = {
    implicit val progressForm = form

    NameModel(
      question = Question(
        postUrl = postUrl,
        backUrl = backUrl.getOrElse(""),
        showBackUrl = backUrl.isDefined,
        number = "6",
        title = pageTitle,
        errorMessages = form.globalErrors.map ( _.message )),
      firstName = TextField(
        key = keys.name.firstName),
      middleNames = TextField(
        key = keys.name.middleNames),
      lastName = TextField(
        key = keys.name.lastName),hasPreviousName = FieldSet(
        classes = if (form(keys.previousName).hasErrors) "invalid" else ""
      ),
      hasPreviousNameTrue = RadioField(
        key = keys.previousName.hasPreviousName, value = "true"),
      hasPreviousNameFalse = RadioField(
        key = keys.previousName.hasPreviousName, value = "false"),

      previousFirstName = TextField(
        key = keys.previousName.previousName.firstName),
      previousMiddleNames = TextField(
        key = keys.previousName.previousName.middleNames),
      previousLastName = TextField(
        key = keys.previousName.previousName.lastName)
    )
  }

  def nameMustache(form: ErrorTransformForm[InprogressForces], call: Call, backUrl: Option[String]): Html = {
    val data = transformFormStepToMustacheData(form, call.url, backUrl)
    val content = Mustache.render("forces/name", data)
    MainStepTemplate(content, pageTitle)
  }
}
