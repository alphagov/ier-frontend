package uk.gov.gds.ier.transaction.forces.waysToVote

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{ForcesStep, Routes, Step}
import controllers.step.forces.routes.{WaysToVoteController, OpenRegisterController}
import controllers.step.forces.{ProxyVoteController, ContactController, PostalVoteController}
import uk.gov.gds.ier.model.{WaysToVoteType}
import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.mvc.Call
import play.api.templates.Html
import play.api.mvc.SimpleResult
import uk.gov.gds.ier.model.{WaysToVote,PostalOrProxyVote}
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.assets.RemoteAssets


class WaysToVoteStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets)
  extends ForcesStep
  with WaysToVoteForms
  with WaysToVoteMustache {

  val validation = waysToVoteForm

  val routing: Routes = Routes(
    get = WaysToVoteController.get,
    post = WaysToVoteController.post,
    editGet = WaysToVoteController.editGet,
    editPost = WaysToVoteController.editPost
  )

  override val onSuccess = TransformApplication { application =>
    if (application.waysToVote == Some(WaysToVote(WaysToVoteType.InPerson))) {
        application.copy(postalOrProxyVote = None)
    } else {
      application
    }
  } andThen BranchOn (_.waysToVote) {
    case Some(WaysToVote(WaysToVoteType.InPerson)) => GoToNextIncompleteStep()
    case _ => GoToNextStep()
  }

  def nextStep(currentState: InprogressForces) = {
    currentState.waysToVote.map(_.waysToVoteType) match {
      case Some(WaysToVoteType.InPerson) => ContactController.contactStep
      case Some(WaysToVoteType.ByPost) => PostalVoteController.postalVoteStep
      case Some(WaysToVoteType.ByProxy) => ProxyVoteController.proxyVoteStep
      case _ => throw new IllegalArgumentException("unknown next step")
    }
  }
}
