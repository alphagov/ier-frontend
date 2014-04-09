package uk.gov.gds.ier.transaction.overseas.lastUkAddress

import controllers.step.overseas.routes.{
  LastUkAddressSelectController,
  DateLeftUkController}
import controllers.step.overseas.{
  NameController,
  PassportCheckController}
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.ApplicationType
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{OverseaStepWithNewMustache, Routes}
import uk.gov.gds.ier.form.OverseasFormImplicits
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class LastUkAddressSelectStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService)
  extends OverseaStepWithNewMustache
  with LastUkAddressManualMustache
  with LastUkAddressForms
  with OverseasFormImplicits {

  val validation = lastUkAddressForm

  val previousRoute = Some(DateLeftUkController.get)

  val routes = Routes(
    get = LastUkAddressSelectController.get,
    post = LastUkAddressSelectController.post,
    editGet = LastUkAddressSelectController.editGet,
    editPost = LastUkAddressSelectController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    currentState.identifyApplication match {
      case ApplicationType.RenewerVoter => NameController.nameStep
      case ApplicationType.DontKnow => this
      case _ => PassportCheckController.passportCheckStep
    }
  }

  override val onSuccess = TransformApplication { currentState =>
    val addressWithAddressLine = currentState.lastUkAddress.map {
      addressService.fillAddressLine(_)
    }

    currentState.copy(
      lastUkAddress = addressWithAddressLine,
      possibleAddresses = None
    )
  } andThen GoToNextIncompleteStep()

}
