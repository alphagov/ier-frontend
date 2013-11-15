package uk.gov.gds.ier.step.address

import controllers.step._
import com.google.inject.Inject
import uk.gov.gds.ier.model.Addresses
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.controller.StepController
import play.api.data.Form
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressApplication
import play.api.templates.Html

class AddressController @Inject ()(val serialiser: JsonSerialiser,
                                   val errorTransformer: ErrorTransformer)
  extends StepController
  with WithSerialiser
  with WithErrorTransformer
  with AddressForms {

  val validation: Form[InprogressApplication] = addressForm
  val editPostRoute: Call = routes.AddressController.editPost
  val stepPostRoute: Call = routes.AddressController.post

  def template(form:InProgressForm, call:Call): Html = {
    val possibleAddresses = form(keys.possibleAddresses.jsonList).value match {
      case Some(possibleAddressJS) if !possibleAddressJS.isEmpty => {
        Some(serialiser.fromJson[Addresses](possibleAddressJS))
      }
      case _ => None
    }
    views.html.steps.address(form, call, possibleAddresses)
  }
  def goToNext(currentState: InprogressApplication): SimpleResult = {
    Redirect(routes.PreviousAddressController.get)
  }
}

