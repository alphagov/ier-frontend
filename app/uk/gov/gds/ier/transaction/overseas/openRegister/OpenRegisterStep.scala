package uk.gov.gds.ier.transaction.overseas.openRegister

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import controllers.step.overseas.routes._
import scala.Some
import controllers.step.overseas.WaysToVoteController
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.assets.RemoteAssets

class OpenRegisterStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets
) extends OverseaStep
  with OpenRegisterForms
  with OpenRegisterMustache {

  val validation = openRegisterForm

  val routing = Routes(
    get = OpenRegisterController.get,
    post = OpenRegisterController.post,
    editGet = OpenRegisterController.editGet,
    editPost = OpenRegisterController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    WaysToVoteController.waysToVoteStep
  }

  override def isStepComplete(currentState: InprogressOverseas) = {
    currentState.openRegisterOptin.isDefined
  }
}
