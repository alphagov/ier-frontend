package uk.gov.gds.ier.transaction.overseas.address

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OverseaStepWithNewMustache, Routes}
import controllers.step.overseas.routes.{AddressController, NinoController}
import controllers.step.overseas.OpenRegisterController
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class AddressStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService)
  extends OverseaStepWithNewMustache
  with AddressForms
  with AddressMustache {

  val validation = addressForm
  val routes = Routes(
    get = AddressController.get,
    post = AddressController.post,
    editGet = AddressController.editGet,
    editPost = AddressController.editPost
  )
  val previousRoute = Some(NinoController.get)

  def nextStep(currentState: InprogressOverseas) = {
    OpenRegisterController.openRegisterStep
  }
}
