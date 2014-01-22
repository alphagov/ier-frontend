package uk.gov.gds.ier.transaction.ordinary.otherAddress

import uk.gov.gds.ier.validation.{FormKeys, ErrorTransformForm, InProgressForm}
import uk.gov.gds.ier.model.{InprogressOrdinary, OtherAddress, InprogressApplication}
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache
import java.net.URL


trait OtherAddressMustache extends StepMustache {

  case class OtherAddressModel(question: Question,
                               hasOtherAddress: Field,
                               hasOtherAddressTrue: Field,
                               hasOtherAddressFalse: Field)

  def transformFormStepToMustacheData(form: ErrorTransformForm[InprogressOrdinary], postUrl: String, backUrl: Option[String]): OtherAddressModel = {
    OtherAddressModel(
      question = Question(
        postUrl = postUrl,
        backUrl = backUrl.getOrElse(""),
        number = "Question 8 of 11",
        title = "Do you live at a second UK address where you're registered to vote?",
        errorMessages = form.globalErrors.map(_.message)
      ),
      hasOtherAddressTrue = Field(
        id = keys.otherAddress.hasOtherAddress.asId("true"),
        name = keys.otherAddress.hasOtherAddress.key,
        attributes = if (form(keys.otherAddress.hasOtherAddress.key).value == Some("true")) "checked=\"checked\"" else ""
      ),
      hasOtherAddressFalse = Field(
        id = keys.otherAddress.hasOtherAddress.asId("false"),
        name = keys.otherAddress.hasOtherAddress.key,
        attributes = if (form(keys.otherAddress.hasOtherAddress.key).value == Some("false")) "checked=\"checked\"" else ""
      ),
      hasOtherAddress = Field(
        classes = if (form(keys.otherAddress.key).hasErrors) "invalid" else ""
      )
    )
  }

  def otherAddressMustache(form: ErrorTransformForm[InprogressOrdinary], call:Call, backUrl: Option[String]) : Html = {
    val data = transformFormStepToMustacheData(form, call.url, backUrl)
    val content = Mustache.render("ordinary/otherAddress", data)
    MainStepTemplate(content, "Register to Vote - Do you live at a second UK address where you're registered to vote?")
  }
}
