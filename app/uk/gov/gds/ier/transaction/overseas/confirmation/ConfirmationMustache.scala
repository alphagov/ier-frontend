package uk.gov.gds.ier.transaction.overseas.confirmation

import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.validation.InProgressForm
import uk.gov.gds.ier.model.InprogressOverseas

trait ConfirmationMustache {
  object Confirmation extends StepMustache {
    def confirmationPage(form:InProgressForm[InprogressOverseas],
                         backUrl: Option[String]) = {
      val content = Mustache.render("overseas/confirmation", None)
      MainStepTemplate(content, "Confirm your details - Register to vote")
    }
  }
}
