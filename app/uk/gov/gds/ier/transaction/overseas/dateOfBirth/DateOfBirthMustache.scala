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
      
    implicit val progressForm = form

    val data = DateOfBirthModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "2 of ?",
        title = "What is your date of birth?"
      ),
      day = TextField(
        key = keys.dob.day
      ),
      month = TextField(
        key = keys.dob.month
      ),
      year = TextField(
        key = keys.dob.year
      )
    )
    val content = Mustache.render("overseas/dateOfBirth", data)
    MainStepTemplate(content, data.question.title)
  }
}
