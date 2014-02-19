package uk.gov.gds.ier.transaction.ordinary.nationality

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressOrdinary
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache


trait NationalityMustache extends StepMustache {

  case class NationalityModel(
      question:Question,
      britishOption: Field,
      irishOption: Field,
      hasOtherCountryOption: Field
  )

  def nationalityMustache(
      form: ErrorTransformForm[InprogressOrdinary],
      postEndpoint: Call,
      backEndpoint:Option[Call]) : Html = {

    implicit val progressForm = form

    val data = NationalityModel(
      question = Question(
        postUrl = postEndpoint.url,
        backUrl = backEndpoint.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "2 of 11",
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
      )
    )
    val content = Mustache.render("ordinary/nationality", data)
    MainStepTemplate(content, "Register to Vote - " + data.question.title)
  }
}
