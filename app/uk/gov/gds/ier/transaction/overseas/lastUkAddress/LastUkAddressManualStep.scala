package uk.gov.gds.ier.transaction.overseas.lastUkAddress

import controllers.step.overseas.routes.{
  LastUkAddressManualController,
  DateLeftUkController}
import controllers.step.overseas.{
  NameController,
  PassportCheckController}
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.ApplicationType
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.form.OverseasFormImplicits
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.assets.RemoteAssets

class LastUkAddressManualStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val remoteAssets: RemoteAssets
) extends OverseaStep
  with LastUkAddressManualMustache
  with LastUkAddressForms
  with OverseasFormImplicits {

  val validation = manualAddressForm

  val routing = Routes(
    get = LastUkAddressManualController.get,
    post = LastUkAddressManualController.post,
    editGet = LastUkAddressManualController.editGet,
    editPost = LastUkAddressManualController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    currentState.identifyApplication match {
      case ApplicationType.RenewerVoter => NameController.nameStep
      case ApplicationType.DontKnow => this
      case _ => PassportCheckController.passportCheckStep
    }
  }
}
