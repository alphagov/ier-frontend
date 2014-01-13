package uk.gov.gds.ier.step.country

import uk.gov.gds.ier.validation.InProgressForm
import uk.gov.gds.ier.model.{InprogressApplication, Country}


class CountryMustacheTransformer {

  case class CountryMustache (postUrl:String = "", england: String = "", scotland: String = "", wales: String = "", northIreland: String = "", channelIslands: String = "", globalErrors:Seq[String] = List.empty )

  def transformFormStepToMustacheData (form:InProgressForm, postUrl:String) : Option[CountryMustache]  = {

     val globalErrors = form.form.globalErrors
     val application = form.form.value
     val countryForm = application.getOrElse(InprogressApplication()).country

     Some (CountryMustache (postUrl,
         if (countryForm.map(_.country) == Some("England")) "checked" else "",
         if (countryForm.map(_.country) == Some("Scotland")) "checked" else "",
         if (countryForm.map(_.country) == Some("Wales")) "checked" else "",
         if (countryForm.map(_.country) == Some("Northern Ireland")) "checked" else "",
         if (countryForm.map(_.country) == Some("British Islands")) "checked" else "",
         globalErrors.map(_.message)
     ))
  }
}
