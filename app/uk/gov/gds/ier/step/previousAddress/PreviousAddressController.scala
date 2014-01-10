package uk.gov.gds.ier.step.previousAddress

import controllers.step._
import com.google.inject.Inject
import uk.gov.gds.ier.model.{InprogressOrdinary, Addresses, PossibleAddress, InprogressApplication}
import uk.gov.gds.ier.step.address.AddressForms
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.controller.OrdinaryController
import play.api.mvc.{SimpleResult, Call}
import play.api.templates.Html

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}

class PreviousAddressController @Inject ()(val serialiser: JsonSerialiser,
                                           val config: Config,
                                           val encryptionService : EncryptionService,
                                           val encryptionKeys : EncryptionKeys)
  extends OrdinaryController
  with AddressForms
  with PreviousAddressForms {

  val validation = previousAddressForm
  val editPostRoute = routes.PreviousAddressController.editPost
  val stepPostRoute = routes.PreviousAddressController.post

  def template(form:InProgressForm[InprogressOrdinary], call:Call): Html = {
    val possibleAddresses = form(keys.possibleAddresses.jsonList).value match {
      case Some(possibleAddressJS) if !possibleAddressJS.isEmpty => {
        serialiser.fromJson[Addresses](possibleAddressJS)
      }
      case _ => Addresses(List.empty)
    }
    val possiblePostcode = form(keys.possibleAddresses.postcode).value

    val possible = possiblePostcode.map(PossibleAddress(possibleAddresses, _))
    views.html.steps.previousAddress(form, call, possible)
  }
  def goToNext(currentState: InprogressOrdinary): SimpleResult = {
    Redirect(routes.OtherAddressController.get)
  }
}

