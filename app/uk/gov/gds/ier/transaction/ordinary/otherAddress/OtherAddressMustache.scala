package uk.gov.gds.ier.transaction.ordinary.otherAddress

import uk.gov.gds.ier.validation.{FormKeys, ErrorTransformForm, InProgressForm}
import uk.gov.gds.ier.model.{InprogressOrdinary, OtherAddress, InprogressApplication}
import play.api.mvc.Call
import play.api.templates.Html
import org.jba.Mustache
import uk.gov.gds.ier.template.MainStepTemplate
import views.html.layouts.{stepsBodyEnd, head}
import play.api.data.Field


trait OtherAddressMustache extends FormKeys {

  case class ModelField(
                     id: String,
                     name: String,
                     value: String
                     )

  case class OtherAddressModel(
                              postUrl: String = "",
                              hasOtherAddressTrue: ModelField,
                              hasOtherAddressFalse: ModelField,
                              globalErrors: Seq[String] = List.empty
                              )

  def transformFormStepToMustacheData(form: ErrorTransformForm[InprogressOrdinary], postUrl: String): OtherAddressModel = {
    val globalErrors = form.globalErrors
    val application = form.value
    val otherAddress = application.getOrElse(InprogressOrdinary()).otherAddress
    OtherAddressModel(postUrl,
        //if (otherAddress.exists(_.hasOtherAddress)) "checked" else "",
        //if (otherAddress.exists(!_.hasOtherAddress)) "checked" else "",
        hasOtherAddressTrue = ModelField(
          id = keys.otherAddress.hasOtherAddress.asId("true"),
          name = keys.otherAddress.hasOtherAddress.key,
          value = if (otherAddress.exists(_.hasOtherAddress)) "checked" else ""
        ),
        hasOtherAddressFalse = ModelField(
          id = keys.otherAddress.hasOtherAddress.asId("false"),
          name = keys.otherAddress.hasOtherAddress.key,
          value = if (otherAddress.exists(!_.hasOtherAddress)) "checked" else ""
        ),
        globalErrors.map(_.message)
    )
  }

  def otherAddressMustache(form: ErrorTransformForm[InprogressOrdinary], call:Call):Html = {
    val data = transformFormStepToMustacheData(form,call.url)
    val content = Mustache.render("ordinary/otherAddress", data)
    MainStepTemplate(content, "Register to Vote - Do you spend part of your time living at another UK address?")
  }
}
