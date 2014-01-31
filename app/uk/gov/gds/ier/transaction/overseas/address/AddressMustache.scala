package uk.gov.gds.ier.transaction.overseas.address

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressOverseas
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.model.CountryWithCode
import uk.gov.gds.ier.validation.constants.NationalityConstants

trait AddressMustache extends StepMustache {

  case class OverseasAddressModel(question:Question, countrySelect: Field, address: Field)

  def addressMustache(form:ErrorTransformForm[InprogressOverseas],
                                 post: Call,
                                 back: Option[Call]): Html = {
	implicit val progressForm = form
	
	def countrySelectOptions (selectedCountry: String) = (NationalityConstants.countryNameToCodes map (
	  isoCountry => {
 	       val isSelected = if (selectedCountry.equals(isoCountry._1)) "selected" else ""
	       SelectOption(isoCountry._1, isoCountry._1, isSelected)
	  }
	)).toList.sortWith((x, y) => x.text.compareTo(y.text) < 0)
	
    val data = OverseasAddressModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "11",
        title = "Where do you live?"
      ),
      countrySelect = SelectField(key = keys.overseasAddress.country, countrySelectOptions(progressForm(keys.overseasAddress.country.key).value.getOrElse(""))),
      address = TextField(key = keys.overseasAddress.overseasAddressDetails)
    )
    val content = Mustache.render("overseas/address", data)
    MainStepTemplate(content, data.question.title)
  }
}
