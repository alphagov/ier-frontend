package uk.gov.gds.ier.transaction.forces.dateOfBirth

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.model.InprogressForces

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
      rangeDontKnow: Field,
      noDobReasonShowFlag: Text
  )

  def transformFormStepToMustacheData(
      form:ErrorTransformForm[InprogressForces],
      post: Call,
      back: Option[Call]): DateOfBirthModel = {
    implicit val progressForm = form

    DateOfBirthModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map (_.url).getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "5",
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
      rangeDontKnow = RadioField(
        key = keys.dob.noDob.range,
        value = "dontKnow"
      ),
      noDobReasonShowFlag = Text (
        value = progressForm(keys.dob.noDob.reason).value.map(noDobReason => "-open").getOrElse("")
      )
    )
  }

  def dateOfBirthMustache(
        form:ErrorTransformForm[InprogressForces],
        post: Call,
        back: Option[Call]): Html = {

    val data = transformFormStepToMustacheData(form, post, back)
    val content = Mustache.render("forces/dateOfBirth", data)
    MainStepTemplate(content, data.question.title)
  }
}
