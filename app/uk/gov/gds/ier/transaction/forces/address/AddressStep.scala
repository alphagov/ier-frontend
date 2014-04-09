package uk.gov.gds.ier.transaction.forces.address

import controllers.step.forces.routes._
import controllers.step.forces.PreviousAddressFirstController
import com.google.inject.Inject
import play.api.mvc.Call
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{ForcesStepWithNewMustache, ForcesStep, Routes}
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.forces.InprogressForces

class AddressStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService)
  extends ForcesStepWithNewMustache
  with AddressLookupMustache
  with AddressForms {

  val validation = addressForm

  val previousRoute = Some(StatementController.get)

  val routes = Routes(
    get = AddressController.get,
    post = AddressController.lookup,
    editGet = AddressController.editGet,
    editPost = AddressController.lookup
  )

  def nextStep(currentState: InprogressForces) = {
    PreviousAddressFirstController.previousAddressFirstStep
  }

  def lookup = ValidSession requiredFor { implicit request => application =>
    lookupAddressForm.bindFromRequest().fold(
      hasErrors => {
        Ok(mustache(hasErrors, routes.post, previousRoute, application).html)
      },
      success => {
        val mergedApplication = success.merge(application)
        Redirect(
          AddressSelectController.get
        ) storeInSession mergedApplication
      }
    )
  }
}
