package uk.gov.gds.ier.transaction.ordinary.dateOfBirth

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.model.InprogressOrdinary

trait DateOfBirthMustache extends StepMustache {

  case class DateOfBirthModel(
      question:Question,
      day: Field,
      month: Field,
      year: Field,
      noDobReason: Field,
      rangeFieldSet: FieldSet,
      rangeUnder18: Field,
      rangeOver70: Field,
      range18to70: Field,
      rangeDontKnow: Field
  )
  
  def transformFormStepToMustacheData(
      form:ErrorTransformForm[InprogressOrdinary],
      post: Call,
      back: Option[Call]): DateOfBirthModel = {
    implicit val progressForm = form

    DateOfBirthModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "3 of 11",
        title = "What is your date of birth?"
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
      rangeFieldSet = FieldSet (
        classes = if (form(keys.dob.noDob.range.key).hasErrors) "invalid" else ""
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
      rangeDontKnow = RadioField(
        key = keys.dob.noDob.range,
        value = "dontKnow"
      )
    )
  }
    def dateOfBirthMustache(
        form:ErrorTransformForm[InprogressOrdinary],
        post: Call,
        back: Option[Call]): Html = {
      
    val data = transformFormStepToMustacheData(form, post, back)
    val content = Mustache.render("ordinary/dateOfBirth", data)
    MainStepTemplate(content, "Register to Vote - " + data.question.title)
  }
}
