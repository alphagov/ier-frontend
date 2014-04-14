package uk.gov.gds.ier.transaction.forces.previousAddress

import controllers.step.forces.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import play.api.mvc.Call
import uk.gov.gds.ier.validation.ErrorTransformForm
import scala.Some
import controllers.step.forces.NationalityController
import uk.gov.gds.ier.transaction.forces.InprogressForces

class PreviousAddressManualStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService)
  extends ForcesStep
  with PreviousAddressManualMustache
  with PreviousAddressForms {

  val validation = manualAddressFormForPreviousAddress

  val previousRoute = Some(PreviousAddressSelectController.get)

  val routes = Routes(
    get = PreviousAddressManualController.get,
    post = PreviousAddressManualController.post,
    editGet = PreviousAddressManualController.editGet,
    editPost = PreviousAddressManualController.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    NationalityController.nationalityStep
  }

}
