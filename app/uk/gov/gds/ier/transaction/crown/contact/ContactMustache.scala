package uk.gov.gds.ier.transaction.crown.contact

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait ContactMustache extends StepTemplate[InprogressCrown] {

  case class ContactModel (
      question:Question,
      contactFieldSet: FieldSet,
      contactEmailCheckbox: Field,
      contactPhoneCheckbox: Field,
      contactPostCheckbox: Field,
      contactEmailText: Field,
      contactPhoneText: Field,
      showEmailFieldFlag: Text) extends MustacheData

  val mustache = MustacheTemplate("crown/contact") { (form, postUrl) =>
    implicit val progressForm = form
    var emailAddress = form(keys.contact.email.detail).value

    if (!emailAddress.isDefined){
      emailAddress = form(keys.postalOrProxyVote.deliveryMethod.emailAddress).value
    }

    val title = "www.gov.uk/register-to-vote - If we have questions about your application, how should we contact you?"
    val newQuestion = "If we have questions about your application, how should we contact you?"
    ContactModel(
      question = Question(
        postUrl = postUrl.url,
        errorMessages = form.globalErrors.map{ _.message },
        title = title,
        newQuestion = newQuestion
      ),
      contactFieldSet = FieldSet(
        classes = if (progressForm(keys.contact).hasErrors) "invalid" else ""
      ),
      contactEmailCheckbox = CheckboxField(
        key = keys.contact.email.contactMe, value = "true"
      ),
      contactPhoneCheckbox = CheckboxField(
        key = keys.contact.phone.contactMe, value = "true"
      ),
      contactPostCheckbox = CheckboxField(
        key = keys.contact.post.contactMe, value = "true"
      ),
      contactEmailText = TextField(
        key = keys.contact.email.detail,
        default = emailAddress
      ),
      contactPhoneText = TextField(
        key = keys.contact.phone.detail
      ),
      showEmailFieldFlag = Text (
        value = if (!form(keys.contact.email.detail).value.isEmpty) "selected" else if
        (!form(keys.postalOrProxyVote.deliveryMethod.emailAddress).value.isEmpty) "selected" else if
        (!form(keys.postalVote.deliveryMethod.emailAddress).value.isEmpty) "selected" else ""
      )
    )
  }
}
