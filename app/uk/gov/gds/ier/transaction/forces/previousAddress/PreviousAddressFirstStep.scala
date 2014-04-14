package uk.gov.gds.ier.transaction.forces.previousAddress

import controllers.step.forces.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.model.{MovedHouseOption}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.service.AddressService

import uk.gov.gds.ier.step.{ForcesStep, Routes}
import uk.gov.gds.ier.transaction.forces.InprogressForces

class PreviousAddressFirstStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val addressService: AddressService)
  extends ForcesStep
  with PreviousAddressFirstMustache
  with PreviousAddressFirstForms {

  val validation = previousAddressFirstForm
  val previousRoute = Some(AddressController.get)

  val routes = Routes(
    get = PreviousAddressFirstController.get,
    post = PreviousAddressFirstController.post,
    editGet = PreviousAddressFirstController.editGet,
    editPost = PreviousAddressFirstController.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    if (currentState.previousAddress.flatMap(_.movedRecently) == Some(MovedHouseOption.Yes)) {
      controllers.step.forces.PreviousAddressPostcodeController.previousPostcodeAddressStep
    } else {
      controllers.step.forces.NationalityController.nationalityStep
    }
  }
}

