package uk.gov.gds.ier.transaction.crown.previousAddress

import controllers.step.crown.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.CrownStepWithNewMustache
import play.api.mvc.Call
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.validation.ErrorTransformForm
import scala.Some
import controllers.step.crown.NationalityController
import uk.gov.gds.ier.transaction.crown.InprogressCrown

class PreviousAddressManualStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService)
  extends CrownStepWithNewMustache
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

  def nextStep(currentState: InprogressCrown) = {
    NationalityController.nationalityStep
  }
}
