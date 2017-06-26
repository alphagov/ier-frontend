package uk.gov.gds.ier.transaction.forces.nationality

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.validation.constants.NationalityConstants
import uk.gov.gds.ier.transaction.forces.InprogressForces


trait NationalityMustache extends StepTemplate[InprogressForces] {

  case class NationalityModel(
      question:Question,
      nationality: FieldSet,
      britishOption: Field,
      irishOption: Field,
      hasOtherCountryOption: Field,
      otherCountry: FieldSet,
      otherCountries0: Field,
      otherCountries1: Field,
      otherCountries2: Field,
      noNationalityReason: Field,
      noNationalityReasonShowFlag: String,
      emailField: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("forces/nationality") { (form, postUrl) =>
    implicit val progressForm = form

    val title = "What is your nationality?"

    val emailAddress = form(keys.contact.email.detail).value

    val nationalityReason = form(keys.nationality.noNationalityReason).value

    val nationalityReasonClass = nationalityReason match {
      case Some("") | None => ""
      case _ => "-open"
    }

    NationalityModel(
      question = Question(
        postUrl = postUrl.url,
        errorMessages = form.globalErrors.map{ _.message },
        title = title
      ),
      nationality = FieldSet(keys.nationality),
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
      otherCountry = FieldSet(keys.nationality.otherCountries),
      otherCountries0 = TextField(keys.nationality.otherCountries.item(0)),
      otherCountries1 = TextField(keys.nationality.otherCountries.item(1)),
      otherCountries2 = TextField(keys.nationality.otherCountries.item(2)),
      noNationalityReason = TextField(keys.nationality.noNationalityReason),
      noNationalityReasonShowFlag = nationalityReasonClass,
      emailField = TextField(
        key = keys.contact.email.detail,
        default = emailAddress
      )
    )
  }
}
