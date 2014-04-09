package uk.gov.gds.ier.transaction.overseas.dateOfBirth

import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait DateOfBirthMustache extends StepTemplate[InprogressOverseas] {

  val title = "What is your date of birth?"

  case class DateOfBirthModel(
      question:Question,
      day: Field,
      month: Field,
      year: Field
  )

  val mustache = MustacheTemplate("overseas/dateOfBirth") { (form, post, back) =>

    implicit val progressForm = form

    val data = DateOfBirthModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "",
        title = title,
        showBackUrl = true
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
    MustacheData(data, title)
  }
}
