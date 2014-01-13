package uk.gov.gds.ier.step.country

import org.jba.Mustache
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.validation.InProgressForm
import uk.gov.gds.ier.model.{InprogressApplication, Country}
import uk.gov.gds.ier.template.MainStepTemplate

trait CountryMustache {

  case class CountryModel(postUrl:String = "", england: String = "", scotland: String = "", wales: String = "", northIreland: String = "", channelIslands: String = "", globalErrors:Seq[String] = List.empty )

  def transformFormStepToMustacheData (form:InProgressForm, postUrl:String) : Option[CountryMustache]  = {

     val globalErrors = form.form.globalErrors
     val application = form.form.value
     val countryForm = application.getOrElse(InprogressApplication()).country

     Some (CountryModel(postUrl,
         if (countryForm.map(_.country) == Some("England")) "checked" else "",
         if (countryForm.map(_.country) == Some("Scotland")) "checked" else "",
         if (countryForm.map(_.country) == Some("Wales")) "checked" else "",
         if (countryForm.map(_.country) == Some("Northern Ireland")) "checked" else "",
         if (countryForm.map(_.country) == Some("British Islands")) "checked" else "",
         globalErrors.map(_.message)
     ))
  }

  def countryMustache(form: InProgressForm, call:Call):Html = {
    val data = transformFormStepToMustacheData(form,call.url).getOrElse(None)
    val content:Html = Mustache.render("ordinary/country", data)
    MainStepTemplate(content, "Register to Vote - Where do you live?")
  }
}
