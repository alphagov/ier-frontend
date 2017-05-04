package uk.gov.gds.ier.transaction.ordinary.alreadyRegistered

import com.google.inject.{Inject, Singleton}
import play.api.mvc.Controller
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OrdinaryStep, Routes}
import uk.gov.gds.ier.transaction.ordinary.{OrdinaryControllers, InprogressOrdinary}
import uk.gov.gds.ier.assets.RemoteAssets


@Singleton
class AlreadyRegisteredStep @Inject ()(
                                   val serialiser: JsonSerialiser,
                                   val config: Config,
                                   val encryptionService : EncryptionService,
                                   val remoteAssets: RemoteAssets,
                                   val ordinary: OrdinaryControllers
                                   ) extends OrdinaryStep
with AlreadyRegisteredMustache
with AlreadyRegisteredForms
{

  val validation = alreadyRegisteredForm

  val routing = Routes(
    get = routes.AlreadyRegisteredStep.get,
    post = routes.AlreadyRegisteredStep.post,
    editGet = routes.AlreadyRegisteredStep.editGet,
    editPost = routes.AlreadyRegisteredStep.editPost
  )

  def nextStep(currentState: InprogressOrdinary) = {
    ordinary.CountryStep
  }
}
