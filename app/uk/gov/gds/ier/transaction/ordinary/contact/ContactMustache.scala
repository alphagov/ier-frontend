package uk.gov.gds.ier.transaction.ordinary.contact

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait ContactMustache extends StepTemplate[InprogressOrdinary] {

  case class ContactModel (
      question:Question,
      contactFieldSet: FieldSet,
      contactEmailCheckbox: Field,
      contactPhoneCheckbox: Field,
      contactPostCheckbox: Field,
      contactEmailText: Field,
      contactPhoneText: Field)

  val mustache = MustacheTemplate("ordinary/contact") { (form, post, back) =>
    implicit val progressForm = form
    val title = "If we have questions about your application," +
                " what's the best way to contact you?"
    
    val emailAddress = form(keys.postalVote.deliveryMethod.emailAddress).value

    val data = ContactModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map { _.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "11",
        title = title
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
      )
    )

    MustacheData(data, title)
  }
}
