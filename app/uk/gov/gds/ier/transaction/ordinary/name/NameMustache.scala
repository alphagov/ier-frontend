package uk.gov.gds.ier.transaction.ordinary.name

import uk.gov.gds.ier.validation.{FormKeys, ErrorTransformForm, InProgressForm}
import uk.gov.gds.ier.model._
import play.api.mvc.Call
import play.api.templates.Html
import org.jba.Mustache
import uk.gov.gds.ier.template.MainStepTemplate
import views.html.layouts.{stepsBodyEnd, head}
import play.api.data.Field
import scala.Some
import uk.gov.gds.ier.model.InprogressOrdinary
import play.api.mvc.Call


trait NameMustache extends FormKeys {

  case class ModelField(
                         id: String,
                         name: String,
                         value: String
                         )

  case class NameModel(
                        postUrl: String = "",
                        name1: ModelField,
                        name2: ModelField,
                        name3: ModelField,
                        hasPreviousNameTrue: ModelField,
                        hasPreviousNameFalse: ModelField,
                        previousName1: ModelField,
                        previousName2: ModelField,
                        previousName3: ModelField,
                        globalErrors: Seq[String] = List.empty
                        )

  def transformFormStepToMustacheData(form: ErrorTransformForm[InprogressOrdinary], postUrl: String): NameModel = {
    val globalErrors = form.globalErrors
    val application = form.value.getOrElse(InprogressOrdinary())
    val name = application.name.getOrElse(Name("", Some(""), "")) // get applicant name from "session" in-progress data
    val hasPreviousName = application.previousName.getOrElse(PreviousName(false, None)).hasPreviousName
    val previousName = application.previousName.getOrElse(PreviousName(false, None)).previousName.getOrElse(Name("", Some(""), ""))

    NameModel(
      postUrl,
      name1 = ModelField(
        id = keys.name.firstName.key,
        name = keys.name.firstName.key,
        value = name.firstName
      ),
      name2 = ModelField(
        id = keys.name.middleNames.key,
        name = keys.name.middleNames.key,
        value = name.middleNames.getOrElse("")
      ),
      name3 = ModelField(
        id = keys.name.lastName.key,
        name = keys.name.lastName.key,
        value = name.lastName
      ),
      hasPreviousNameTrue = ModelField(
        id = keys.previousName.hasPreviousName.asId("true"),
        name = keys.previousName.hasPreviousName.key,
        value = if (hasPreviousName) "checked" else ""
      ),
      hasPreviousNameFalse = ModelField(
        id = keys.previousName.hasPreviousName.asId("false"),
        name = keys.previousName.hasPreviousName.key,
        value = if (hasPreviousName) "checked" else ""
      ),
      previousName1 = ModelField(
        id = keys.previousName.firstName.key,
        name = keys.previousName.firstName.key,
        value = previousName.firstName
      ),
      previousName2 = ModelField(
        id = keys.previousName.middleNames.key,
        name = keys.previousName.middleNames.key,
        value = previousName.middleNames.getOrElse("")
      ),
      previousName3 = ModelField(
        id = keys.previousName.lastName.key,
        name = keys.previousName.lastName.key,
        value = previousName.lastName
      ),
      globalErrors.map(_.message)
    )
  }

  def otherAddressMustache(form: ErrorTransformForm[InprogressOrdinary], call: Call): Html = {
    val data = transformFormStepToMustacheData(form, call.url)
    val content = Mustache.render("ordinary/name", data)
    MainStepTemplate(content, "Register to Vote - What is your name?")
  }
}
