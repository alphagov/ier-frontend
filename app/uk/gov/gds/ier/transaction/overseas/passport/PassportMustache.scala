package uk.gov.gds.ier.transaction.overseas.passport

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressOverseas
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache

trait PassportMustache extends StepMustache {

  case class PassportCheckModel(
      question: Question,
      hasPassport: Field,
      hasPassportTrue: Field,
      hasPassportFalse: Field,
      bornInUk: Field,
      bornInUkTrue: Field,
      bornInUkFalse: Field
  )

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

  case class CitizenDetailsModel(
      question: Question,
      hasPassport: Field,
      bornInUk: Field,
      howBecameCitizen: Field,
      citizenDate: Field,
      citizenDateDay: Field,
      citizenDateMonth: Field,
      citizenDateYear: Field
  )

  object PassportMustache {
    def passportCheckData(
        form: ErrorTransformForm[InprogressOverseas],
        postEndpoint: Call,
        backEndpoint: Option[Call]) = {
      implicit val progressForm = form
      PassportCheckModel(
        question = Question(
          postUrl = postEndpoint.url,
          backUrl = backEndpoint.map { call => call.url }.getOrElse(""),
          errorMessages = form.globalErrors.map{ _.message },
          number = "7",
          title = "Do you have a British Passport?"
        ),
        hasPassport = Field(
          classes = if (form(keys.passport.hasPassport.key).hasErrors) {
            "invalid"
          } else ""
        ),
        hasPassportTrue = RadioField (
          key = keys.passport.hasPassport,
          value = "true"
        ),
        hasPassportFalse = RadioField (
          key = keys.passport.hasPassport,
          value = "false"
        ),
        bornInUk = Field(
          classes = if (form(keys.passport.bornInsideUk.key).hasErrors) {
            "invalid"
          } else ""
        ),
        bornInUkTrue = RadioField (
          key = keys.passport.bornInsideUk,
          value = "true"
        ),
        bornInUkFalse = RadioField (
          key = keys.passport.bornInsideUk,
          value = "false"
        )
      )
    }

    def passportCheckPage(
        form:ErrorTransformForm[InprogressOverseas],
        postEndpoint: Call,
        backEndpoint: Option[Call]): Html = {
      val data = passportCheckData(form, postEndpoint, backEndpoint)
      val content = Mustache.render("overseas/passportCheck", data)

      MainStepTemplate(content, data.question.title)
    }

    def passportDetailsData(
        form:ErrorTransformForm[InprogressOverseas],
        postEndpoint: Call,
        backEndpoint: Option[Call]) = {
      implicit val progressForm = form
      PassportDetailsModel(
        question = Question(
          postUrl = postEndpoint.url,
          backUrl = backEndpoint.map { call => call.url }.getOrElse(""),
          errorMessages = form.globalErrors.map{ _.message },
          number = "7",
          title = "What are your Passport details?"
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
          classes = if (form(keys.passport.passportDetails.issueDate.key).hasErrors) {
            "invalid"
          } else ""
        )
      )
    }

    def passportDetailsPage(
        form:ErrorTransformForm[InprogressOverseas],
        postEndpoint: Call,
        backEndpoint: Option[Call]): Html = {
      val data = passportDetailsData(form, postEndpoint, backEndpoint)
      val content = Mustache.render("overseas/passportDetails", data)

      MainStepTemplate(content, data.question.title)
    }

    def citizenDetailsData(
        form:ErrorTransformForm[InprogressOverseas],
        postEndpoint: Call,
        backEndpoint: Option[Call]) = {
      implicit val progressForm = form
      CitizenDetailsModel(
        question = Question(
          postUrl = postEndpoint.url,
          backUrl = backEndpoint.map { call => call.url }.getOrElse(""),
          errorMessages = form.globalErrors.map{ _.message },
          number = "7",
          title = "When and how did you become a British citizen?"
        ),
        hasPassport =      TextField(keys.passport.hasPassport),
        bornInUk =         TextField(keys.passport.bornInsideUk),
        howBecameCitizen = TextField(keys.passport.citizenDetails.howBecameCitizen),
        citizenDateDay =   TextField(keys.passport.citizenDetails.dateBecameCitizen.day),
        citizenDateMonth = TextField(keys.passport.citizenDetails.dateBecameCitizen.month),
        citizenDateYear =  TextField(keys.passport.citizenDetails.dateBecameCitizen.year),
        citizenDate = Field(
          id = keys.passport.citizenDetails.dateBecameCitizen.asId(),
          classes = if (form(keys.passport.citizenDetails.dateBecameCitizen.key).hasErrors) {
            "invalid"
          } else ""
        )
      )
    }

    def citizenDetailsPage(
        form:ErrorTransformForm[InprogressOverseas],
        postEndpoint: Call,
        backEndpoint: Option[Call]): Html = {
      val data = citizenDetailsData(form, postEndpoint, backEndpoint)
      val content = Mustache.render("overseas/citizenDetails", data)

      MainStepTemplate(content, data.question.title)
    }
  }
}

