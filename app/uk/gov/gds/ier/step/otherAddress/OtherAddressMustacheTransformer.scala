package uk.gov.gds.ier.step.otherAddress

import uk.gov.gds.ier.validation.InProgressForm
import uk.gov.gds.ier.model.{OtherAddress, InprogressApplication}


class OtherAddressMustacheTransformer {

  case class OtherAddressMustache(
                              postUrl: String = "",
                              hasOtherAddressTrue: String = "",
                              hasOtherAddressFalse: String = "",
                              globalErrors: Seq[String] = List.empty
                              )

  def transformFormStepToMustacheData(form: InProgressForm, postUrl: String): Option[OtherAddressMustache] = {
    val globalErrors = form.form.globalErrors
    val application = form.form.value
    val otherAddress = application.getOrElse(InprogressApplication()).otherAddress
    Some(OtherAddressMustache(postUrl,
        if (otherAddress.exists(_.hasOtherAddress)) "checked" else "",
        if (otherAddress.exists(!_.hasOtherAddress)) "checked" else "",
        globalErrors.map(_.message)
    ))
  }
}
