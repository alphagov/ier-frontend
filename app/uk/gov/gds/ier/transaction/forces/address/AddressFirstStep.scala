package uk.gov.gds.ier.transaction.forces.address

import controllers.step.forces.routes._
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
    controllers.step.forces.AddressController.addressStep
  }

  override val onSuccess = GoToNextStep()
}

