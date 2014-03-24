package uk.gov.gds.ier.transaction.crown.nationality

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressCrown
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache


trait NationalityMustache extends StepMustache {

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

  def transformFormStepToMustacheData(
      application:InprogressCrown,
      form: ErrorTransformForm[InprogressCrown],
      postEndpoint: Call,
      backEndpoint:Option[Call]) : NationalityModel = {

    implicit val progressForm = form

    val otherCountriesList = application.nationality.map(_.otherCountries).getOrElse(List.empty)

    NationalityModel(
      question = Question(
        postUrl = postEndpoint.url,
        backUrl = backEndpoint.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "3",
        title = "What is your nationality?"
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
        classes = if (progressForm(keys.nationality.otherCountries.key).hasErrors) "invalid" else ""
      ),

      otherCountriesTail =
        if (!otherCountriesList.isEmpty) createMustacheCountryList(otherCountriesList.tail)
        else List.empty,
      moreThanOneOtherCountry = otherCountriesList.size > 1,
      noNationalityReason= TextField(
        key = keys.nationality.noNationalityReason
      ),
      noNationalityReasonShowFlag = Text (
        value = progressForm(keys.nationality.noNationalityReason.key).value.map(noNationalityReason => "-open").getOrElse("")
      )
    )
  }

  def nationalityMustache(
      application:InprogressCrown,
      form: ErrorTransformForm[InprogressCrown],
      postEndpoint: Call,
      backEndpoint:Option[Call]) : Html = {
    val data = transformFormStepToMustacheData(application, form, postEndpoint, backEndpoint)
    val content = Mustache.render("crown/nationality", data)
    MainStepTemplate(content, data.question.title)
  }

  def createMustacheCountryList (otherCountriesTail:List[String]) : List[CountryItem] = {
    otherCountriesTail.zipWithIndex.map{case (item, i) => CountryItem((i+2).toString,item)}
  }
}
