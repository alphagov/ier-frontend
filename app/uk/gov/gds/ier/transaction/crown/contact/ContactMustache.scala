package uk.gov.gds.ier.transaction.crown.contact

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressCrown
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache

trait ContactMustache extends StepMustache {

  case class ContactModel (
      question:Question,
      contactFieldSet: FieldSet,
      contactEmailCheckbox: Field,
      contactPhoneCheckbox: Field,
      contactPostCheckbox: Field,
      contactEmailText: Field,
      contactPhoneText: Field)

  def transformFormStepToMustacheData(
      application: InprogressCrown,
      form: ErrorTransformForm[InprogressCrown],
      postEndpoint: Call, backEndpoint:Option[Call]) : ContactModel = {

    implicit val progressForm = form
    ContactModel(
      question = Question(
        postUrl = postEndpoint.url,
        backUrl = backEndpoint.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "12",
        title = "If we have questions about your application, how should we contact you?"
      ),
      contactFieldSet = FieldSet(
        classes = if (progressForm(keys.contact.key).hasErrors) "invalid" else ""
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
        key = keys.contact.email.detail
      ),
      contactPhoneText = TextField(
        key = keys.contact.phone.detail
      )
    )
  }

  def contactMustache(
      application: InprogressCrown,
      form:ErrorTransformForm[InprogressCrown],
      postEndpoint: Call,
      backEndpoint: Option[Call]): Html = {

    val data = transformFormStepToMustacheData(application, form, postEndpoint, backEndpoint)
    val content = Mustache.render("crown/contact", data)
    MainStepTemplate(content, data.question.title)
  }
}
