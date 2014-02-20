package uk.gov.gds.ier.transaction.overseas.parentName

import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.model.InprogressOverseas
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.validation.ErrorTransformForm

trait ParentNameMustache extends StepMustache {

  val pageTitle = "Register to Vote - Parent or guardian's registration details"

  case class ParentNameModel(
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

    def transformFormStepToMustacheData(form: ErrorTransformForm[InprogressOverseas], 
                                        postUrl: String, 
                                        backUrl: Option[String]): ParentNameModel = {
    implicit val progressForm = form

    ParentNameModel(
      question = Question(
        postUrl = postUrl,
        backUrl = backUrl.getOrElse(""),
        showBackUrl = backUrl.isDefined,
        number = "6",
        title = pageTitle,
        errorMessages = form.globalErrors.map { _.message }),
      firstName = TextField(
        key = keys.overseasParentName.parentName.firstName),
      middleNames = TextField(
        key = keys.overseasParentName.parentName.middleNames),
      lastName = TextField(
        key = keys.overseasParentName.parentName.lastName),
      hasPreviousName = FieldSet(
        classes = if (form(keys.overseasParentName.parentPreviousName.key).hasErrors) "invalid" else ""
      ),
      hasPreviousNameTrue = RadioField(
        key = keys.overseasParentName.parentPreviousName.hasPreviousName, value = "true"),
      hasPreviousNameFalse = RadioField(
        key = keys.overseasParentName.parentPreviousName.hasPreviousName, value = "false"),

      previousFirstName = TextField(
        key = keys.overseasParentName.parentPreviousName.previousName.firstName),
      previousMiddleNames = TextField(
        key = keys.overseasParentName.parentPreviousName.previousName.middleNames),
      previousLastName = TextField(
        key = keys.overseasParentName.parentPreviousName.previousName.lastName)
    )
  }

  def parentNameMustache(form: ErrorTransformForm[InprogressOverseas], call: Call, backUrl: Option[String]): Html = {
    val data = transformFormStepToMustacheData(form, call.url, backUrl)
    val content = Mustache.render("overseas/parentName", data)
    MainStepTemplate(content, pageTitle)
  }
}
