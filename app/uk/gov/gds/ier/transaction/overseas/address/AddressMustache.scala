package uk.gov.gds.ier.transaction.overseas.address

import uk.gov.gds.ier.validation.constants.NationalityConstants
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait AddressMustache extends StepTemplate[InprogressOverseas] {

  val title = "Where should we write to you about your registration?"

  case class AddressModel(
      question:Question,
      countrySelect: Field,
      addressLine1: Field,
      addressLine2: Field,
      addressLine3: Field,
      addressLine4: Field,
      addressLine5: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("overseas/address") { (form, post) =>

    implicit val progressForm = form

    AddressModel(
      question = Question(
        postUrl = post.url,
        title = title,
        errorMessages = form.globalErrors.map { _.message }
      ),
      countrySelect = SelectField(key = keys.overseasAddress.country,
        optionList = countrySelectOptions(
          progressForm(keys.overseasAddress.country).value.getOrElse("")),
        default = SelectOption("", "Please select your country")),
      addressLine1 = TextField(key = keys.overseasAddress.addressLine1),
      addressLine2 = TextField(key = keys.overseasAddress.addressLine2),
      addressLine3 = TextField(key = keys.overseasAddress.addressLine3),
      addressLine4 = TextField(key = keys.overseasAddress.addressLine4),
      addressLine5 = TextField(key = keys.overseasAddress.addressLine5)
    )
  }

  def countrySelectOptions(selectedCountry: String) = (NationalityConstants.countryNameToCodes map (
    isoCountry => {
      val isSelected = if (selectedCountry.equals(isoCountry._2.displayName)) "selected" else ""
      SelectOption(isoCountry._2.displayName, isoCountry._2.displayName, isSelected)
    }
  )).toList.sortWith((x, y) => x.text.compareTo(y.text) < 0)


}
