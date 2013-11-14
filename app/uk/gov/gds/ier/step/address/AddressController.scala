package uk.gov.gds.ier.step.address

import controllers._
import com.google.inject.Inject
import uk.gov.gds.ier.model.{PossibleAddress, Addresses}
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
  val editPostRoute: Call = step.routes.AddressController.editPost
  val stepPostRoute: Call = step.routes.AddressController.post

  def template(form:InProgressForm, call:Call): Html = {
    val possibleAddresses = form(keys.possibleAddresses.jsonList).value match {
      case Some(possibleAddressJS) if !possibleAddressJS.isEmpty => {
        serialiser.fromJson[Addresses](possibleAddressJS)
      }
      case _ => Addresses(List.empty)
    }
    val possiblePostcode = form(keys.possibleAddresses.postcode).value

    val possible = possiblePostcode.map(PossibleAddress(possibleAddresses.addresses, _))
    views.html.steps.address(form, call, possible)
  }
  def goToNext(currentState: InprogressApplication): SimpleResult = {
    Redirect(routes.RegisterToVoteController.registerStep("previous-address"))
  }
}

