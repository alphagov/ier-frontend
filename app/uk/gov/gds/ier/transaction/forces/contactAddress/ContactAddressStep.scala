package uk.gov.gds.ier.transaction.forces.contactAddress

import controllers.step.forces.OpenRegisterController
import controllers.step.forces.routes.{ContactAddressController, RankController}
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import uk.gov.gds.ier.transaction.forces.InprogressForces

class ContactAddressStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)
  extends ForcesStep
    with ContactAddressForms
    with ContactAddressMustache{

  val validation = contactAddressForm

  val routes = Routes(
    get = ContactAddressController.get,
    post = ContactAddressController.post,
    editGet = ContactAddressController.editGet,
    editPost = ContactAddressController.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    OpenRegisterController.openRegisterStep
  }
}

