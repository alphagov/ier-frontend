package uk.gov.gds.ier.step.previousAddress

import controllers._
import com.google.inject.Inject
import uk.gov.gds.ier.model.Addresses
import uk.gov.gds.ier.step.address.AddressForms
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.controller.StepController
import play.api.data.Form
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressApplication
import play.api.templates.Html

class PreviousAddressController @Inject ()(val serialiser: JsonSerialiser,
                                           val errorTransformer: ErrorTransformer)
  extends StepController
  with WithSerialiser
  with WithErrorTransformer
  with AddressForms 
  with PreviousAddressForms {

  val validation: Form[InprogressApplication] = previousAddressForm
  val editPostRoute: Call = step.routes.PreviousAddressController.editPost
  val stepPostRoute: Call = step.routes.PreviousAddressController.post

  def template(form:InProgressForm, call:Call): Html = {
    val possibleAddresses = form(keys.possibleAddresses.jsonList).value match {
      case Some(possibleAddressJS) if !possibleAddressJS.isEmpty => {
        Some(serialiser.fromJson[Addresses](possibleAddressJS))
      }
      case _ => None
    }
    views.html.steps.previousAddress(form, call, possibleAddresses)
  }
  def goToNext(currentState: InprogressApplication): SimpleResult = {
    Redirect(routes.RegisterToVoteController.registerStep("other-address"))
  }
}

