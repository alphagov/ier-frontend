package uk.gov.gds.ier.transaction.overseas.address

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressOverseas
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.model.CountryWithCode
import uk.gov.gds.ier.validation.constants.NationalityConstants

trait AddressMustache extends StepMustache {

  case class AddressModel(
      question:Question,
      countrySelect: Field,
      addressLine1: Field,
      addressLine2: Field,
      addressLine3: Field,
      addressLine4: Field,
      addressLine5: Field)

  def transformFormStepToMustacheData(form:ErrorTransformForm[InprogressOverseas],
                                 post: Call,
                                 back: Option[Call]): AddressModel = {
	implicit val progressForm = form

	def countrySelectOptions(selectedCountry: String) = (NationalityConstants.countryNameToCodes map (
	  isoCountry => {
 	       val isSelected = if (selectedCountry.equals(isoCountry._2.displayName)) "selected" else ""
	       SelectOption(isoCountry._2.displayName, isoCountry._2.displayName, isSelected)
	  }
	)).toList.sortWith((x, y) => x.text.compareTo(y.text) < 0)

    AddressModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "",
        title = "Where do you live?"
      ),
      countrySelect = SelectField(key = keys.overseasAddress.country,
          optionList = countrySelectOptions(
              progressForm(keys.overseasAddress.country.key).value.getOrElse("")),
          default = SelectOption("", "Please select your country")),
      addressLine1 = TextField(key = keys.overseasAddress.addressLine1),
      addressLine2 = TextField(key = keys.overseasAddress.addressLine2),
      addressLine3 = TextField(key = keys.overseasAddress.addressLine3),
      addressLine4 = TextField(key = keys.overseasAddress.addressLine4),
      addressLine5 = TextField(key = keys.overseasAddress.addressLine5)
    )
  }
  def addressMustache(form:ErrorTransformForm[InprogressOverseas],
                                 post: Call,
                                 back: Option[Call]): Html = {
	val data = transformFormStepToMustacheData(form, post, back)
	val content = Mustache.render("overseas/address", data)
    MainStepTemplate(content, data.question.title)
  }
}
