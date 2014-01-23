package uk.gov.gds.ier.mustache
import play.api.templates.Html
import views.html.layouts.{stepsBodyEnd, head}
import uk.gov.gds.ier.validation.FormKeys

trait StepMustache extends FormKeys {

  def Mustache = org.jba.Mustache

  def MainStepTemplate(content:Html, title: String, header:Html = head(), scripts:Html = stepsBodyEnd()) = {
    views.html.layouts.main (title = Some(title),stylesheets = header, scripts = scripts)(content)
  }

  case class FieldSet(classes:String = "")
  case class Field(id:String = "", name:String = "", classes:String = "", wrapperClasses:String ="", value:String = "", attributes:String = "")
  case class Question(postUrl:String = "", backUrl:String = "", number:String = "", title:String = "", errorMessages:Seq[String] = Seq.empty)
}
