package uk.gov.gds.ier.transaction.country

import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.{Country}
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait CountryMustache extends StepMustache {
  case class CountryModel(
      postUrl:String = "",
      countries:FieldSet,
      england:Field,
      scotland:Field,
      wales:Field,
      northIreland:Field,
      channelIslands:Field,
      livingAbroad:Field,
      origin:FieldSet,
      englandOrigin:Field,
      scotlandOrigin:Field,
      walesOrigin:Field,
      northIrelandOrigin:Field,
      channelIslandsOrigin:Field,
      globalErrors:Seq[String] = List.empty
  )

  def transformFormStepToMustacheData(
      implicit form:ErrorTransformForm[InprogressOrdinary],
      postUrl:String):CountryModel = {

    val globalErrors = form.globalErrors
    def makeCountry(country:String) = {
      val isChecked = form(keys.country.residence).value match {
        case Some(`country`) => "checked=\"checked\""
        case _ => ""
      }
      Field(
        id = keys.country.residence.asId(country),
        name = keys.country.residence.key,
        attributes = isChecked
      )
    }

    val countriesFieldSet = FieldSet(
      if (form(keys.country.residence).hasErrors) "invalid" else ""
    )

    CountryModel(postUrl,
      countries = countriesFieldSet,
      england = RadioField(keys.country.residence, "England"),
      scotland = RadioField(keys.country.residence, "Scotland"),
      wales = RadioField(keys.country.residence, "Wales"),
      northIreland = RadioField(keys.country.residence, "Northern Ireland"),
      channelIslands = RadioField(keys.country.residence, "British Islands"),
      livingAbroad = RadioField(keys.country.residence, "Abroad"),
      origin = FieldSet(
        if (form(keys.country.origin).hasErrors) "invalid" else ""
      ),
      englandOrigin = RadioField(keys.country.origin, "England"),
      scotlandOrigin = RadioField(keys.country.origin, "Scotland"),
      walesOrigin = RadioField(keys.country.origin, "Wales"),
      northIrelandOrigin = RadioField(keys.country.origin, "Northern Ireland"),
      channelIslandsOrigin = RadioField(keys.country.origin, "British Islands"),
      globalErrors.map(_.message)
    )
  }

  def countryMustache(form: ErrorTransformForm[InprogressOrdinary], call:Call):Html = {
    val data = transformFormStepToMustacheData(form,call.url)
    val content = Mustache.render("ordinary/country", data)
    MainStepTemplate(content, "Where do you live?")
  }
}
