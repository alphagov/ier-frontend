package uk.gov.gds.ier.transaction.country

import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.{InprogressOrdinary, Country}
import uk.gov.gds.ier.mustache.StepMustache

trait CountryMustache extends StepMustache {
  case class CountryModel(postUrl:String = "",
                          countries:FieldSet,
                          england:Field,
                          scotland:Field,
                          wales:Field,
                          northIreland:Field,
                          channelIslands:Field,
                          globalErrors:Seq[String] = List.empty )

  def transformFormStepToMustacheData (form:ErrorTransformForm[InprogressOrdinary], postUrl:String):CountryModel = {
    val globalErrors = form.globalErrors

    def makeRadio(country:String) = {
      Field(
        id = keys.country.residence.asId(country),
        name = keys.country.residence.key,
        attributes = if (form(keys.country.residence.key).value == Some(country)) "checked" else ""
      )
    }

    val countriesFieldSet = FieldSet(
      if (form(keys.country.residence.key).hasErrors) "invalid" else ""
    )

    CountryModel(postUrl,
      countries = countriesFieldSet,
      england = makeRadio("England"),
      scotland = makeRadio("Scotland"),
      wales = makeRadio("Wales"),
      northIreland = makeRadio("Northern Ireland"),
      channelIslands = makeRadio("British Islands"),
      globalErrors.map(_.message)
    )
  }

  def countryMustache(form: ErrorTransformForm[InprogressOrdinary], call:Call):Html = {
    val data = transformFormStepToMustacheData(form,call.url)
    val content = Mustache.render("ordinary/country", data)
    MainStepTemplate(content, "Register to Vote - Where do you live?")
  }
}
