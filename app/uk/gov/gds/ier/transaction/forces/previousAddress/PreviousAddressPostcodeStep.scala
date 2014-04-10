package uk.gov.gds.ier.transaction.forces.previousAddress

import controllers.step.forces.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{ForcesStepWithNewMustache, Routes}
import uk.gov.gds.ier.transaction.forces.InprogressForces

class PreviousAddressPostcodeStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService)
  extends ForcesStepWithNewMustache
  with PreviousAddressPostcodeMustache
  with PreviousAddressForms {

  val validation = postcodeAddressFormForPreviousAddress

  val previousRoute = Some(PreviousAddressFirstController.get)

  val routes = Routes(
    get = PreviousAddressPostcodeController.get,
    post = PreviousAddressPostcodeController.lookup,
    editGet = PreviousAddressPostcodeController.editGet,
    editPost = PreviousAddressPostcodeController.lookup
  )

  def nextStep(currentState: InprogressForces) = {
    controllers.step.forces.PreviousAddressSelectController.previousAddressSelectStep
  }

  def lookup = ValidSession requiredFor { implicit request => application =>
    validation.bindFromRequest().fold(
      hasErrors => {
        Ok(mustache(hasErrors, routes.post, previousRoute, application).html)
      },
      success => {
        val mergedApplication = success.merge(application)
        Redirect(
          PreviousAddressSelectController.get
        ) storeInSession mergedApplication
      }
    )
  }
}
