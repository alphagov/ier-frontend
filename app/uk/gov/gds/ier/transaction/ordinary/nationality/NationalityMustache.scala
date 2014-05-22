package uk.gov.gds.ier.transaction.ordinary.nationality

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.transaction.ordinary.confirmation.ConfirmationMustache
import uk.gov.gds.ier.form.OrdinaryFormImplicits

trait NationalityMustache extends StepTemplate[InprogressOrdinary]
  with OrdinaryFormImplicits {

  case class CountryItem (
      index:String = "",
      countryName:String = ""
  )

  case class NationalityModel(
      question:Question,
      britishOption: Field,
      irishOption: Field,
      hasOtherCountryOption: Field,
      otherCountriesHead: Field,
      otherCountriesTail: List[CountryItem] = List.empty,
      moreThanOneOtherCountry: Boolean,
      noNationalityReason: Field,
      noNationalityReasonShowFlag: Text
  ) extends MustacheData

  val mustache = MultilingualTemplate("ordinary/nationality") { implicit lang =>
    (form, postEndpoint) =>

    implicit val progressForm = form
    val otherCountriesList = form.obtainOtherCountriesList

    NationalityModel(
      question = Question(
        postUrl = postEndpoint.url,
        errorMessages = Messages.translatedGlobalErrors(form),
        number = "2 " + Messages("step_of") + " 11",
        title = Messages("ordinary_nationality_title")
      ),
      britishOption = CheckboxField(
        key = keys.nationality.british,
        value = "true"
      ),
      irishOption = CheckboxField(
        key = keys.nationality.irish,
        value = "true"
      ),
      hasOtherCountryOption = CheckboxField(
        key = keys.nationality.hasOtherCountry,
        value = "true"
      ),
      otherCountriesHead =  Field(
        id = keys.nationality.otherCountries.item(0).asId(),
        name = keys.nationality.otherCountries.item(0).key,
        value = otherCountriesList match {
          case Nil => ""
          case headCountry :: tailCountries => headCountry
        },
        classes = if (progressForm(keys.nationality.otherCountries.item(0)).hasErrors) "invalid" else ""
      ),

      otherCountriesTail =
        if (!otherCountriesList.isEmpty) createMustacheCountryList(otherCountriesList.tail)
        else List.empty,
      moreThanOneOtherCountry = otherCountriesList.size > 1,
      noNationalityReason= TextField(
        key = keys.nationality.noNationalityReason
      ),
      noNationalityReasonShowFlag = Text (
        value = progressForm(keys.nationality.noNationalityReason).value.map(noNationalityReason => "-open").getOrElse("")
      )
    )
  }

  def createMustacheCountryList (otherCountriesTail:List[String]) : List[CountryItem] = {
    otherCountriesTail.zipWithIndex.map{case (item, i) => CountryItem((i+2).toString,item)}
  }
}
