package uk.gov.gds.ier.step.otherAddress

import controllers._
import com.google.inject.Inject
import uk.gov.gds.ier.model.Addresses
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.controller.StepController
import play.api.data.Form
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressApplication
import play.api.templates.Html

class OtherAddressController @Inject ()(val serialiser: JsonSerialiser,
                                        val errorTransformer: ErrorTransformer)
  extends StepController
  with WithSerialiser
  with WithErrorTransformer
  with OtherAddressForms {

  val validation: Form[InprogressApplication] = otherAddressForm
  val editPostRoute: Call = step.routes.OtherAddressController.editPost
  val stepPostRoute: Call = step.routes.OtherAddressController.post

  def template(form:InProgressForm, call:Call): Html = {
    views.html.steps.otherAddress(form, call)
  }
  def goToNext(currentState: InprogressApplication): SimpleResult = {
    Redirect(routes.RegisterToVoteController.registerStep("open-register"))
  }
}

