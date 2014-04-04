package uk.gov.gds.ier.transaction.crown.waysToVote

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.CrownStep
import controllers.step.crown.routes.{WaysToVoteController, OpenRegisterController}
import controllers.step.crown.{ProxyVoteController, ContactController, PostalVoteController}
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.model.{WaysToVoteType}
import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.model.WaysToVote
import uk.gov.gds.ier.transaction.crown.InprogressCrown


class WaysToVoteStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)
  extends CrownStep
  with WaysToVoteForms
  with WaysToVoteMustache {

  val validation = waysToVoteForm

  val routes: Routes = Routes(
    get = WaysToVoteController.get,
    post = WaysToVoteController.post,
    editGet = WaysToVoteController.editGet,
    editPost = WaysToVoteController.editPost
  )
  val previousRoute = Some(OpenRegisterController.get)

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

  def nextStep(currentState: InprogressCrown) = {
    currentState.waysToVote.map(_.waysToVoteType) match {
      case Some(WaysToVoteType.InPerson) => ContactController.contactStep
      case Some(WaysToVoteType.ByPost) => PostalVoteController.postalVoteStep
      case Some(WaysToVoteType.ByProxy) => ProxyVoteController.proxyVoteStep
      case _ => throw new IllegalArgumentException("unknown next step")
    }
  }

  def template(form: ErrorTransformForm[InprogressCrown], call:Call, backUrl: Option[Call]): Html = {
    waysToVoteMustache(form, call, backUrl.map(_.url))
  }
}
