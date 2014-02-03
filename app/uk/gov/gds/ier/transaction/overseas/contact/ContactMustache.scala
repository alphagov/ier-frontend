package uk.gov.gds.ier.transaction.overseas.contact

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressOverseas
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache

trait ContactMustache extends StepMustache {

  case class ContactModel (question:Question,
                           contactFieldSet: FieldSet,
                           contactEmailCheckbox: Field,
                           contactPhoneCheckbox: Field,
                           contactPostCheckbox: Field,
                           contactEmailText: Field,
                           contactPhoneText: Field)

  def contactMustache(form:ErrorTransformForm[InprogressOverseas],
                         post: Call,
                         back: Option[Call]): Html = {

    implicit val progressForm = form

    val data = ContactModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "12",
        title = "If we have questions about your application, how should we contact you?"
      ),
      contactFieldSet = FieldSet(
        classes = if (progressForm(keys.contact.key).hasErrors) "invalid" else ""
      ),
      contactEmailCheckbox = CheckboxField(
        key = keys.contact.email.contactMe
      ),
      contactPhoneCheckbox = CheckboxField(
        key = keys.contact.phone.contactMe
      ),
      contactPostCheckbox = CheckboxField(
        key = keys.contact.post.contactMe
      ),
      contactEmailText = TextField(
        key = keys.contact.email.detail
      ),
      contactPhoneText = TextField(
        key = keys.contact.phone.detail
      )
    )
    val content = Mustache.render("overseas/contact", data)
    MainStepTemplate(content, data.question.title)
  }
}
