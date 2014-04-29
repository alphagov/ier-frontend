package uk.gov.gds.ier.transaction.forces.nationality

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.validation.constants.NationalityConstants
import uk.gov.gds.ier.transaction.forces.InprogressForces


trait NationalityMustache extends StepTemplate[InprogressForces] {

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
  )

  val mustache = MustacheTemplate("forces/nationality") { (form, postUrl) =>
    implicit val progressForm = form

    val otherCountriesList =  obtainOtherCountriesList(progressForm)

    val title = "What is your nationality?"

    val data = NationalityModel(
      question = Question(
        postUrl = postUrl.url,
        errorMessages = form.globalErrors.map{ _.message },
        number = "4",
        title = title
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
        id = keys.nationality.otherCountries.asId() + "[0]",
        name = keys.nationality.otherCountries.key + "[0]",
        value = otherCountriesList match {
          case Nil => ""
          case headCountry :: tailCountries => headCountry
        },
        classes = if (progressForm(keys.nationality.otherCountries).hasErrors) "invalid" else ""
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

    MustacheData(data, title)
  }

  def createMustacheCountryList (otherCountriesTail:List[String]) : List[CountryItem] = {
    otherCountriesTail.zipWithIndex.map{case (item, i) => CountryItem((i+2).toString,item)}
  }

  def obtainOtherCountriesList(form: ErrorTransformForm[InprogressForces]):List[String] = {
    (
      for (i <- 0 until NationalityConstants.numberMaxOfOtherCountries
           if (form(otherCountriesKey(i)).value.isDefined)
             && !form(otherCountriesKey(i)).value.get.isEmpty)
      yield form(otherCountriesKey(i)).value.get
      ).toList
  }

  def otherCountriesKey(i:Int) = keys.nationality.otherCountries.item(i)
}
