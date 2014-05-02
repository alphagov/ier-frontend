package uk.gov.gds.ier.transaction.country

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.{Country}
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

    CountryModel(
      question = Question(
        title = "Where do you live?",
        postUrl = post.url,
        errorMessages = globalErrors.map(_.message)
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
