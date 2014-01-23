package uk.gov.gds.ier.mustache
import play.api.templates.Html
import views.html.layouts.{stepsBodyEnd, head}
import uk.gov.gds.ier.validation.{ErrorTransformForm, FormKeys}
import uk.gov.gds.ier.model.InprogressOrdinary

trait StepMustache extends FormKeys {

  def Mustache = org.jba.Mustache

  def MainStepTemplate(content:Html, title: String, header:Html = head(), scripts:Html = stepsBodyEnd()) = {
    views.html.layouts.main (title = Some(title),stylesheets = header, scripts = scripts)(content)
  }

  case class FieldSet(classes:String = "")
  case class Field(id:String = "", name:String = "", invalidInputClass:String = "", invalidWrapperClass:String ="", value:String = "", attributes:String = "")
  case class Question(postUrl:String = "", backUrl:String = "", showBackUrl:Boolean = true, number:String = "", title:String = "", errorMessages:Seq[String] = Seq.empty)

  object TextField {
    def apply(key: uk.gov.gds.ier.validation.Key)(implicit progressForm: ErrorTransformForm[InprogressOrdinary]): Field = {
      Field(
        id = key.asId(),
        name = key.key,
        value = progressForm(key.key).value.getOrElse(""),
        invalidWrapperClass = if (progressForm(key.key).hasErrors) "validation-wrapper-invalid" else "",
        invalidInputClass = if (progressForm(key.key).hasErrors) "invalid" else "")
    }
  }

  object RadioField {
    def apply(key: uk.gov.gds.ier.validation.Key, value: String)(implicit progressForm: ErrorTransformForm[InprogressOrdinary]): Field = {
      Field(
        id = key.asId(value),
        name = key.key,
        value = if (progressForm(key.key).value.exists(_ == value)) "checked" else "",
        invalidWrapperClass = if (progressForm(key.key).hasErrors) "invalid" else "")
    }
  }
}
