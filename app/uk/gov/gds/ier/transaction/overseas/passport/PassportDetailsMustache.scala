package uk.gov.gds.ier.transaction.overseas.passport

import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait PassportDetailsMustache extends StepTemplate[InprogressOverseas] {

  val title = "What are your Passport details?"

  case class PassportDetailsModel(
      question: Question,
      hasPassport: Field,
      bornInUk: Field,
      passportNumber: Field,
      authority: Field,
      issueDate: Field,
      issueDateDay: Field,
      issueDateMonth: Field,
      issueDateYear: Field
  )

  val mustache = MustacheTemplate("overseas/passportDetails") { (form, post, back) =>

    implicit val progressForm = form

    val data = PassportDetailsModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "7",
        title = title
      ),
      hasPassport =    TextField(keys.passport.hasPassport),
      bornInUk =       TextField(keys.passport.bornInsideUk),
      passportNumber = TextField(keys.passport.passportDetails.passportNumber),
      authority =      TextField(keys.passport.passportDetails.authority),
      issueDateDay =   TextField(keys.passport.passportDetails.issueDate.day),
      issueDateMonth = TextField(keys.passport.passportDetails.issueDate.month),
      issueDateYear =  TextField(keys.passport.passportDetails.issueDate.year),
      issueDate = Field(
        id = keys.passport.passportDetails.issueDate.asId(),
        classes = if (form(keys.passport.passportDetails.issueDate).hasErrors) {
          "invalid"
        } else ""
      )
    )
    MustacheData(data, title)
  }
}

