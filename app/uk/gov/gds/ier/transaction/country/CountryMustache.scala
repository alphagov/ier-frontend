package uk.gov.gds.ier.transaction.country

import org.jba.Mustache
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.{InprogressOrdinary, Country}
import uk.gov.gds.ier.template.MainStepTemplate

trait CountryMustache {

  case class CountryModel(postUrl:String = "", england: String = "", scotland: String = "", wales: String = "", northIreland: String = "", channelIslands: String = "", globalErrors:Seq[String] = List.empty )

  def transformFormStepToMustacheData (form:ErrorTransformForm[InprogressOrdinary], postUrl:String, backUrl: Option[String]):CountryModel = {
    val globalErrors = form.globalErrors 
    val countryForm = form.value.flatMap{ application => application.country}

    CountryModel(postUrl,
      if (countryForm.map(_.country) == Some("England")) "checked" else "",
      if (countryForm.map(_.country) == Some("Scotland")) "checked" else "",
      if (countryForm.map(_.country) == Some("Wales")) "checked" else "",
      if (countryForm.map(_.country) == Some("Northern Ireland")) "checked" else "",
      if (countryForm.map(_.country) == Some("British Islands")) "checked" else "",
      globalErrors.map(_.message)
    )
  }

  def countryMustache(form: ErrorTransformForm[InprogressOrdinary], call:Call):Html = {
    val data = transformFormStepToMustacheData(form,call.url)
    val content = Mustache.render("ordinary/country", data)
    MainStepTemplate(content, "Register to Vote - Where do you live?")
  }
}
