package uk.gov.gds.ier.transaction.overseas.previousName

import uk.gov.gds.ier.transaction.overseas.OverseasControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class PreviousNameStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val overseas: OverseasControllers
) extends OverseaStep
  with PreviousNameForms
  with PreviousNameMustache {

  val validation = previousNameForm

  val routing = Routes(
    get = routes.PreviousNameStep.get,
    post = routes.PreviousNameStep.post,
    editGet = routes.PreviousNameStep.editGet,
    editPost = routes.PreviousNameStep.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    overseas.NinoStep
  }
}