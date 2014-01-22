package uk.gov.gds.ier.transaction.ordinary.name

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.templates.Html
import uk.gov.gds.ier.model.InprogressOrdinary
import play.api.mvc.Call
import uk.gov.gds.ier.mustache.StepMustache

trait NameMustache extends StepMustache {

  val pageTitle = "Register to Vote - What is your full name?"

  case class ModelField(
    id: String,
    name: String,
    value: String,
    invalidWrapperClass: String = "",
    invalidInputClass: String = "")

  case class NameModel(
    question: Question,
    firstName: ModelField,
    middleNames: ModelField,
    lastName: ModelField,
    hasPreviousName: FieldSet,
    hasPreviousNameTrue: ModelField,
    hasPreviousNameFalse: ModelField,
    previousFirstName: ModelField,
    previousMiddleNames: ModelField,
    previousLastName: ModelField)

  object ModelTextField {
    def apply(key: uk.gov.gds.ier.validation.Key)(implicit form: ErrorTransformForm[InprogressOrdinary]): ModelField = {
      ModelField(
        id = key.asId(),
        name = key.key,
        value = form(key.key).value.getOrElse(""),
        invalidWrapperClass = if (form(key.key).hasErrors) "validation-wrapper-invalid" else "",
        invalidInputClass = if (form(key.key).hasErrors) "invalid" else "")
    }
  }

  object ModelRadioField {
    def apply(key: uk.gov.gds.ier.validation.Key, value: String)(implicit form: ErrorTransformForm[InprogressOrdinary]): ModelField = {
      ModelField(
        id = key.asId(value),
        name = key.key,
        value = if (form(key.key).value.exists(_ == value)) "checked" else "",
        invalidWrapperClass = if (form(key.key).hasErrors) "invalid" else "")
    }
  }

  def transformFormStepToMustacheData(form1: ErrorTransformForm[InprogressOrdinary], postUrl: String, backUrl: Option[String]): NameModel = {
    implicit val form = form1
    val globalErrors = form.globalErrors
    NameModel(
      question = Question(
        postUrl = postUrl,
        backUrl = backUrl.getOrElse(""),
        showBackUrl = backUrl.isDefined,
        number = "4 of 11",
        title = pageTitle,
        errorMessages = form.globalErrors.map { _.message }),
        
      firstName = ModelTextField(
        key = keys.name.firstName),
      middleNames = ModelTextField(
        key = keys.name.middleNames),
      lastName = ModelTextField(
        key = keys.name.lastName),

      hasPreviousName = FieldSet(if (form(keys.previousName.key).hasErrors) "invalid" else ""),
      hasPreviousNameTrue = ModelRadioField(
        key = keys.previousName.hasPreviousName, value = "true"),
      hasPreviousNameFalse = ModelRadioField(
        key = keys.previousName.hasPreviousName, value = "false"),

      previousFirstName = ModelTextField(
        key = keys.previousName.previousName.firstName),
      previousMiddleNames = ModelTextField(
        key = keys.previousName.previousName.middleNames),
      previousLastName = ModelTextField(
        key = keys.previousName.previousName.lastName))
  }

  def nameMustache(form: ErrorTransformForm[InprogressOrdinary], call: Call, backUrl: Option[String]): Html = {
    val data = transformFormStepToMustacheData(form, call.url, backUrl)
    val content = Mustache.render("ordinary/name", data)
    MainStepTemplate(content, pageTitle)
  }
}
