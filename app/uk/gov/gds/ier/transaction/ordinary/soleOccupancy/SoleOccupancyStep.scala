package uk.gov.gds.ier.transaction.ordinary.soleOccupancy

import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.model._
import play.api.templates.Html

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.OrdinaryStep
import play.api.mvc.Call
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.validation.ErrorTransformForm
import scala.Some
import uk.gov.gds.ier.transaction.ordinary.{OrdinaryControllers, InprogressOrdinary}
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class SoleOccupancyStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val ordinary: OrdinaryControllers
) extends OrdinaryStep
  with SoleOccupancyForms
  with SoleOccupancyMustache {

  val validation = soleOccupancyForm

  val routing = Routes(
    get = routes.SoleOccupancyStep.get,
    post = routes.SoleOccupancyStep.post,
    editGet = routes.SoleOccupancyStep.editGet,
    editPost = routes.SoleOccupancyStep.editPost
  )

  override val onSuccess = GoToNextIncompleteStep()

  def nextStep(currentState: InprogressOrdinary) = {
    ordinary.ContactStep
  }
}

