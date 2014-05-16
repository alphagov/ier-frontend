package uk.gov.gds.ier.transaction.country

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait CountryMustache extends StepTemplate[InprogressOrdinary] {
  case class CountryModel(
      question:Question,
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
      channelIslandsOrigin:Field
  ) extends MustacheData

  val mustache = MustacheTemplate("ordinary/country") { (form, post) =>
    implicit val progressForm = form

    val countriesFieldSet = FieldSet(
      if (form(keys.country.residence).hasErrors) "invalid" else ""
    )

    CountryModel(
      question = Question(
        title = Messages("ordinary_country_heading"),
        number = "1 of 11",
        postUrl = post.url,
        errorMessages = Messages.translatedGlobalErrors(form)
      ),
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
      channelIslandsOrigin = RadioField(keys.country.origin, "British Islands")
    )
  }
}
