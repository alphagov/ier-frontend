package uk.gov.gds.ier.transaction.crown.address

import uk.gov.gds.ier.transaction.crown.CrownControllers
import controllers.step.crown.routes._
import com.google.inject.{Inject, Singleton}
import play.api.mvc.Call
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.{LastAddress, HasAddressOption}
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.{CrownStep, Routes}
import uk.gov.gds.ier.validation.ErrorTransformForm
import controllers.step.crown.{PreviousAddressFirstController, NationalityController}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class AddressManualStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val remoteAssets: RemoteAssets,
    val crown: CrownControllers
) extends CrownStep
  with AddressManualMustache
  with AddressForms {

  val validation = manualAddressForm

  val routing = Routes(
    get = AddressManualController.get,
    post = AddressManualController.post,
    editGet = AddressManualController.editGet,
    editPost = AddressManualController.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    import HasAddressOption._

    currentState.address.flatMap(_.hasAddress) match {
      case Some(YesAndLivingThere) => crown.PreviousAddressFirstStep
      case Some(YesAndNotLivingThere) => crown.PreviousAddressFirstStep
      case _ => crown.NationalityStep
    }
  }
}
