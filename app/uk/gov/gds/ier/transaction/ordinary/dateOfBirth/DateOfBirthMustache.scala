package uk.gov.gds.ier.transaction.ordinary.dateOfBirth

import uk.gov.gds.ier.validation.{CountryValidator, ErrorTransformForm}
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import play.api.data.validation.Constraint
import uk.gov.gds.ier.model.Country

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
      rangeOver70: Field,
      range18to70: Field,
      range14to15_YoungScot: Field,
      range16to17_YoungScot: Field,
      rangeOver18_YoungScot: Field,
      rangeDontKnow: Field,
      noDobReasonShowFlag: Text
  ) extends MustacheData

  val mustache = MultilingualTemplate("ordinary/dateOfBirth") { implicit lang => (form, post) =>
    implicit val progressForm = form

    val country = (form(keys.country.residence).value, form(keys.country.origin).value) match {
      case (Some("Abroad"), origin) => Country(origin.getOrElse(""), true)
      case (residence, _) => Country(residence.getOrElse(""), false)
    }

    DateOfBirthModel(
      question = Question(
        postUrl = post.url,
        errorMessages =  Messages.translatedGlobalErrors(form),
        number = s"3 ${Messages("step_of")} 11",
        title = Messages("ordinary_dob_title")
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
      isScot = CountryValidator.isScotland(Some(country)),
      rangeFieldSet = FieldSet (
        classes = if (form(keys.dob.noDob.range).hasErrors) "invalid" else ""
      ),
      rangeUnder18 = RadioField(
        key = keys.dob.noDob.range,
        value = "under18"
      ),
      range18to70 = RadioField(
        key = keys.dob.noDob.range,
        value = "18to70"
      ),
      rangeOver70 = RadioField(
        key = keys.dob.noDob.range,
        value = "over70"
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
      )
    )
  }
}

