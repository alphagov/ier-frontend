package uk.gov.gds.ier.step.previousAddress

import controllers.step._
import com.google.inject.Inject
import uk.gov.gds.ier.model.{Addresses, PossibleAddress}
import uk.gov.gds.ier.step.address.AddressForms
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.controller.StepController
import play.api.data.Form
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressApplication
import play.api.templates.Html

class PreviousAddressController @Inject ()(val serialiser: JsonSerialiser)
  extends StepController
  with WithSerialiser
  with AddressForms
  with PreviousAddressForms {

  val validation = previousAddressForm
  val editPostRoute = routes.PreviousAddressController.editPost
  val stepPostRoute = routes.PreviousAddressController.post

  def template(form:InProgressForm, call:Call): Html = {
    val possibleAddresses = form(keys.possibleAddresses.jsonList).value match {
      case Some(possibleAddressJS) if !possibleAddressJS.isEmpty => {
        serialiser.fromJson[Addresses](possibleAddressJS)
      }
      case _ => Addresses(List.empty)
    }
    val possiblePostcode = form(keys.possibleAddresses.postcode).value

    val possible = possiblePostcode.map(PossibleAddress(possibleAddresses.addresses, _))
    views.html.steps.previousAddress(form, call, possible)
  }
  def goToNext(currentState: InprogressApplication): SimpleResult = {
    Redirect(routes.OtherAddressController.get)
  }
}

