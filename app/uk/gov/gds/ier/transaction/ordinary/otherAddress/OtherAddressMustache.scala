package uk.gov.gds.ier.transaction.ordinary.otherAddress

import uk.gov.gds.ier.validation.{FormKeys, ErrorTransformForm, InProgressForm}
import uk.gov.gds.ier.model.{InprogressOrdinary, OtherAddress, InprogressApplication}
import uk.gov.gds.ier.model.OtherAddress._
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache
import java.net.URL


trait OtherAddressMustache extends StepMustache {

  case class OtherAddressModel(question: Question,
                               hasOtherAddress: Field,
                               hasOtherAddressStudent: Field,
                               hasOtherAddressHome: Field,
                               hasOtherAddressNone: Field)

  def transformFormStepToMustacheData(form: ErrorTransformForm[InprogressOrdinary],
                                      postUrl: String,
                                      backUrl: Option[String]): OtherAddressModel = {
    val otherAddressValue = form(keys.otherAddress.hasOtherAddress.key).value
    OtherAddressModel(
      question = Question(
        postUrl = postUrl,
        backUrl = backUrl.getOrElse(""),
        number = "8 of 11",
        title = "Do you also live at a second address?",
        errorMessages = form.globalErrors.map(_.message)
      ),
      hasOtherAddressStudent = Field(
        id = keys.otherAddress.hasOtherAddress.asId(StudentOtherAddress.name),
        name = keys.otherAddress.hasOtherAddress.key,
        attributes = if (otherAddressValue == Some(StudentOtherAddress.name)) {
          "checked=\"checked\""
        } else {
          ""
        }
      ),
      hasOtherAddressHome = Field(
        id = keys.otherAddress.hasOtherAddress.asId(HomeOtherAddress.name),
        name = keys.otherAddress.hasOtherAddress.key,
        attributes = if (otherAddressValue == Some(HomeOtherAddress.name)) {
          "checked=\"checked\""
        } else {
          ""
        }
      ),
      hasOtherAddressNone = Field(
        id = keys.otherAddress.hasOtherAddress.asId(NoOtherAddress.name),
        name = keys.otherAddress.hasOtherAddress.key,
        attributes = if (otherAddressValue == Some(NoOtherAddress.name)) {
          "checked=\"checked\""
        } else {
          ""
        }
      ),
      hasOtherAddress = Field(
        classes = if (form(keys.otherAddress.key).hasErrors) "invalid" else ""
      )
    )
  }

  def otherAddressMustache(form: ErrorTransformForm[InprogressOrdinary],
                           call:Call,
                           backUrl: Option[String]) : Html = {
    val data = transformFormStepToMustacheData(form, call.url, backUrl)
    val content = Mustache.render("ordinary/otherAddress", data)
    MainStepTemplate (
      content,
      "Register to Vote - Do you also live at a second address?"
    )
  }
}
