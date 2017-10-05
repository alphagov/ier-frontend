package uk.gov.gds.ier.transaction.ordinary.dateOfBirth

import uk.gov.gds.ier.validation.{CountryValidator, ErrorTransformForm}
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import play.api.data.validation.Constraint
import uk.gov.gds.ier.model.Country
import uk.gov.gds.ier.service.ScotlandService

trait DateOfBirthMustache extends StepTemplate[InprogressOrdinary] {

  case class DateOfBirthModel(
      question:Question,
      day: Field,
      month: Field,
      year: Field,
      noDobReason: Field,
      isScot: Boolean,
      rangeFieldSet: FieldSet,
      rangeUnder18: Field,
      rangeOver75: Field,
      range18to75: Field,
      range14to15_YoungScot: Field,
      range16to17_YoungScot: Field,
      rangeOver18_YoungScot: Field,
      rangeDontKnow: Field,
      noDobReasonShowFlag: Text,
      emailField: Field
  ) extends MustacheData

  val scotlandService: ScotlandService

  val mustache = MultilingualTemplate("ordinary/dateOfBirth") { implicit lang => (form, post) =>
    implicit val progressForm = form

    val country = (form(keys.country.residence).value, form(keys.country.origin).value) match {
      case (Some("Abroad"), origin) => Country(origin.getOrElse(""), true)
      case (residence, _) => Country(residence.getOrElse(""), false)
    }

    val emailAddress = form(keys.contact.email.detail).value

    val postcode = form(keys.address.postcode).value.getOrElse("").toUpperCase

    DateOfBirthModel(
      question = Question(
        postUrl = post.url,
        errorMessages =  Messages.translatedGlobalErrors(form),
        title = Messages("title") +" "+ Messages("ordinary_dob_title"),
        newQuestion = Messages("ordinary_dob_title")
      ),
      day = TextField(
        key = keys.dob.dob.day
      ),
      month = TextField(
        key = keys.dob.dob.month
      ),
      year = TextField(
        key = keys.dob.dob.year
      ),
      noDobReason = TextField(
        key = keys.dob.noDob.reason
      ),
      isScot = scotlandService.isScotByPostcodeOrCountry(postcode, country),
        rangeFieldSet = FieldSet (
        classes = if (form(keys.dob.noDob.range).hasErrors) "invalid" else ""
      ),
      rangeUnder18 = RadioField(
        key = keys.dob.noDob.range,
        value = "under18"
      ),
      range18to75 = RadioField(
        key = keys.dob.noDob.range,
        value = "18to75"
      ),
      rangeOver75 = RadioField(
        key = keys.dob.noDob.range,
        value = "over75"
      ),
      range14to15_YoungScot = RadioField(
        key = keys.dob.noDob.range,
        value = "14to15"
      ),
      range16to17_YoungScot = RadioField(
        key = keys.dob.noDob.range,
        value = "16to17"
      ),
      rangeOver18_YoungScot = RadioField(
        key = keys.dob.noDob.range,
        value = "over18"
      ),
      rangeDontKnow = RadioField(
        key = keys.dob.noDob.range,
        value = "dontKnow"
      ),
      noDobReasonShowFlag = Text (
        value = progressForm(keys.dob.noDob.reason).value.map(noDobReason => "-open").getOrElse("")
      ),
      emailField = TextField(
        key = keys.contact.email.detail,
        default = emailAddress
      )
    )
  }
}

