package uk.gov.gds.ier.transaction.overseas.parentsAddress

import controllers.step.overseas.routes._
import controllers.step.overseas.PassportCheckController
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class ParentsAddressManualStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService)
  extends OverseaStep
  with ParentsAddressManualMustache
  with ParentsAddressForms {

  val validation = parentsManualAddressForm

  val previousRoute = Some(DateLeftUkController.get)

  val routes = Routes(
    get = ParentsAddressManualController.get,
    post = ParentsAddressManualController.post,
    editGet = ParentsAddressManualController.editGet,
    editPost = ParentsAddressManualController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    PassportCheckController.passportCheckStep
  }
}
