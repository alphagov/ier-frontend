package uk.gov.gds.ier.transaction.ordinary.name

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.templates.Html
import uk.gov.gds.ier.model.InprogressOrdinary
import play.api.mvc.Call
import uk.gov.gds.ier.mustache.StepMustache


trait NameMustache extends StepMustache {

  case class ModelField(
                         id: String,
                         name: String,
                         value: String,
                         invalidWrapperClass: String = "",
                         invalidInputClass: String = ""
                         )

  case class NameModel(
                        postUrl: String = "",
                        showBackUrl:Boolean,
                        backUrl:String,
                        firstName: ModelField,
                        middleNames: ModelField,
                        lastName: ModelField,
                        hasPreviousNameTrue: ModelField,
                        hasPreviousNameFalse: ModelField,
                        previousFirstName: ModelField,
                        previousMiddleNames: ModelField,
                        previousLastName: ModelField,
                        globalErrors: Seq[String] = List.empty
                        )

  def transformFormStepToMustacheData(form: ErrorTransformForm[InprogressOrdinary], postUrl: String, backUrl: Option[String]): NameModel = {
    val globalErrors = form.globalErrors
    NameModel(
      postUrl,
      backUrl.isDefined,
      backUrl.getOrElse(""),
      firstName = ModelField(
        id = keys.name.firstName.asId(),
        name = keys.name.firstName.key,
        value = form(keys.name.firstName.key).value.getOrElse("")
      ),
      middleNames = ModelField(
        id = keys.name.middleNames.asId(),
        name = keys.name.middleNames.key,
        value = form(keys.name.middleNames.key).value.getOrElse("")
      ),
      lastName = ModelField(
        id = keys.name.lastName.asId(),
        name = keys.name.lastName.key,
        value = form(keys.name.lastName.key).value.getOrElse("")
      ),
      hasPreviousNameTrue = ModelField(
        id = keys.previousName.hasPreviousName.asId("true"),
        name = keys.previousName.hasPreviousName.key,
        value = if (form(keys.hasPreviousName.key).value.isDefined) "checked" else ""
      ),
      hasPreviousNameFalse = ModelField(
        id = keys.previousName.hasPreviousName.asId("false"),
        name = keys.previousName.hasPreviousName.key,
        value = if (!form(keys.hasPreviousName.key).value.isDefined) "checked" else ""
      ),
      previousFirstName = ModelField(
        id = keys.previousName.firstName.asId(),
        name = keys.previousName.firstName.key,
        value = form(keys.previousName.firstName.key).value.getOrElse("")
      ),
      previousMiddleNames = ModelField(
        id = keys.previousName.middleNames.asId(),
        name = keys.previousName.middleNames.key,
        value = form(keys.previousName.middleNames.key).value.getOrElse("")
      ),
      previousLastName = ModelField(
        id = keys.previousName.lastName.asId(),
        name = keys.previousName.lastName.key,
        value = form(keys.previousName.lastName.key).value.getOrElse("")
      ),
      globalErrors.map(_.message)
    )
  }

  def nameMustache(form: ErrorTransformForm[InprogressOrdinary], call: Call, backUrl: Option[String]): Html = {
    val data = transformFormStepToMustacheData(form, call.url, backUrl)
    val content = Mustache.render("ordinary/name", data)
    MainStepTemplate(content, "Register to Vote - What is your name?")
  }
}
