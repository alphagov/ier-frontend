package uk.gov.gds.ier.transaction.forces.address

import controllers.step.forces.routes._
import controllers.step.forces.AddressController._
import controllers.step.forces.AddressManualController._
import controllers.step.forces.AddressSelectController._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.service.AddressService

import uk.gov.gds.ier.step.{Routes, ForcesStep}
import uk.gov.gds.ier.transaction.forces.InprogressForces

class AddressFirstStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val addressService: AddressService)
  extends ForcesStep
  with AddressFirstMustache
  with AddressFirstForms
  with AddressForms {

  val validation = addressFirstForm
  val previousRoute = Some(StatementController.get)

  val routes = Routes(
    get = AddressFirstController.get,
    post = AddressFirstController.post,
    editGet = AddressFirstController.editGet,
    editPost = AddressFirstController.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    currentState.address.map(_.address) match {
      case Some(address) =>
        if (address.exists(_.postcode.isEmpty)) addressStep
        else if (address.exists(_.manualAddress.isDefined)) addressManualStep
        else if (address.exists(_.uprn.isDefined)) addressSelectStep
        else addressStep
      case _ => addressStep
    }
  }

  override val onSuccess = TransformApplication { currentState =>
    if (currentState.address.exists(_.hasUkAddress == Some(false))) {
      currentState.copy(previousAddress = None)
    }
    else currentState
  } andThen GoToNextStep()
}

