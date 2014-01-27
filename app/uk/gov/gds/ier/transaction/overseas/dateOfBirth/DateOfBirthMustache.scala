package uk.gov.gds.ier.transaction.overseas.dateOfBirth

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressOverseas
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache

trait DateOfBirthMustache extends StepMustache {

  case class DateOfBirthModel(question:Question,
                                       day: Field,
                                       month: Field,
                                       year: Field)

  def dateOfBirthMustache(form:ErrorTransformForm[InprogressOverseas],
                                 post: Call,
                                 back: Option[Call]): Html = {

    val data = DateOfBirthModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "2 of ?",
        title = "What is your date of birth?"
      ),
      day = Field(
        name = keys.dob.day.key,
        id = keys.dob.day.asId(),
        value = form(keys.dob.day.key).value.getOrElse(""),
        classes = if (form(keys.dob.day.key).hasErrors) "invalid" else ""
      ),
      month = Field(
        name = keys.dob.month.key,
        id = keys.dob.month.asId(),
        value = form(keys.dob.month.key).value.getOrElse(""),
        classes = if (form(keys.dob.month.key).hasErrors) "invalid" else ""
      ),
      year = Field(
        name = keys.dob.year.key,
        id = keys.dob.year.asId(),
        value = form(keys.dob.year.key).value.getOrElse(""),
        classes = if (form(keys.dob.year.key).hasErrors) "invalid" else ""
      )
    )
    val content = Mustache.render("overseas/dateOfBirth", data)
    MainStepTemplate(content, data.question.title)
  }
}
