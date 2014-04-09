package uk.gov.gds.ier.transaction.ordinary.otherAddress

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.OtherAddress._
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait OtherAddressMustache extends StepTemplate[InprogressOrdinary] {

  case class OtherAddressModel(
      question: Question,
      hasOtherAddress: Field,
      hasOtherAddressStudent: Field,
      hasOtherAddressHome: Field,
      hasOtherAddressNone: Field
  )

  val mustache = MustacheTemplate("ordinary/otherAddress") {
    (form, post, back) =>

    val otherAddressValue = form(keys.otherAddress.hasOtherAddress).value
    val data = OtherAddressModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map(_.url).getOrElse(""),
        number = "7 of 11",
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
        classes = if (form(keys.otherAddress).hasErrors) "invalid" else ""
      )
    )

    MustacheData(data, data.question.title)
  }
}

