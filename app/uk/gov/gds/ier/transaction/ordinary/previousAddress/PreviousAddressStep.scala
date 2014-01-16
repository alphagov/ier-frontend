package uk.gov.gds.ier.transaction.ordinary.previousAddress

import controllers.step.ordinary.OtherAddressController
import controllers.step.ordinary.routes.{PreviousAddressController, AddressController}
import com.google.inject.Inject
import uk.gov.gds.ier.model.{InprogressOrdinary, Addresses, PossibleAddress}
import uk.gov.gds.ier.transaction.ordinary.address.AddressForms
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.{SimpleResult, Call}
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.{Routes, OrdinaryStep}

class PreviousAddressStep @Inject ()(val serialiser: JsonSerialiser,
                                     val config: Config,
                                     val encryptionService : EncryptionService,
                                     val encryptionKeys : EncryptionKeys)
  extends OrdinaryStep
  with AddressForms
  with PreviousAddressForms {

  val validation = previousAddressForm
  val previousRoute = Some(AddressController.get)

  val routes = Routes(
    get = PreviousAddressController.get,
    post = PreviousAddressController.post,
    editGet = PreviousAddressController.editGet,
    editPost = PreviousAddressController.editPost
  )

  def template(form:InProgressForm[InprogressOrdinary], call:Call, backUrl: Option[Call]): Html = {
    val possibleAddresses = form(keys.possibleAddresses.jsonList).value match {
      case Some(possibleAddressJS) if !possibleAddressJS.isEmpty => {
        serialiser.fromJson[Addresses](possibleAddressJS)
      }
      case _ => Addresses(List.empty)
    }
    val possiblePostcode = form(keys.possibleAddresses.postcode).value

    val possible = possiblePostcode.map(PossibleAddress(possibleAddresses, _))
    views.html.steps.previousAddress(form, call, possible, backUrl.map(_.url))
  }
  def nextStep(currentState: InprogressOrdinary) = {
    OtherAddressController.otherAddressStep
  }
}

