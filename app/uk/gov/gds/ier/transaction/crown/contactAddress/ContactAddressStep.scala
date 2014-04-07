package uk.gov.gds.ier.transaction.crown.contactAddress

import controllers.step.crown.OpenRegisterController
import controllers.step.crown.routes.{ContactAddressController, NinoController}
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{CrownStepWithNewMustache, Routes}
import uk.gov.gds.ier.transaction.crown.InprogressCrown

class ContactAddressStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)
  extends CrownStepWithNewMustache
    with ContactAddressForms
    with ContactAddressMustache{

  val validation = contactAddressForm
  val previousRoute = Some(NinoController.get)

  val routes = Routes(
    get = ContactAddressController.get,
    post = ContactAddressController.post,
    editGet = ContactAddressController.editGet,
    editPost = ContactAddressController.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    OpenRegisterController.openRegisterStep
  }
}

