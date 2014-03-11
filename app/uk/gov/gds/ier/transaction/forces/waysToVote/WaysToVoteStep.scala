package uk.gov.gds.ier.transaction.forces.waysToVote

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.ForcesStep
import controllers.step.forces.routes.WaysToVoteController
import controllers.step.forces.routes.OpenRegisterController
import controllers.step.forces.{ProxyVoteController, ContactController, PostalVoteController}
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.model.{WaysToVoteType, InprogressForces}
import uk.gov.gds.ier.validation.InProgressForm
import play.api.mvc.Call
import play.api.templates.Html
import play.api.mvc.SimpleResult
import uk.gov.gds.ier.step.NextStep
import uk.gov.gds.ier.model.WaysToVote
import uk.gov.gds.ier.model.PostalOrProxyVote


class WaysToVoteStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val encryptionKeys : EncryptionKeys)
  extends ForcesStep
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

  def nextStep(currentState: InprogressForces): ForcesStep = {
    currentState.waysToVote.map(_.waysToVoteType) match {
      case Some(WaysToVoteType.InPerson) => ContactController.contactStep
      case Some(WaysToVoteType.ByPost) => PostalVoteController.postalVoteStep
      case Some(WaysToVoteType.ByProxy) => ProxyVoteController.proxyVoteStep
      case _ => throw new IllegalArgumentException("unknown next step")
    }
  }
  
  override def goToNext(currentState: InprogressForces):SimpleResult = { 
    currentState.waysToVote match {
      case None => Redirect(routes.get)
      case Some(waysToVote) => {
        currentState.postalOrProxyVote match {
          case Some(postalOrProxyVote) 
            if (postalOrProxyVote.typeVote != waysToVote.waysToVoteType &&
                waysToVote.waysToVoteType != WaysToVoteType.InPerson) => 
              Redirect(nextStep(currentState).routes.get)
          case _ => nextStep(currentState).goToNext(currentState) 
        }
      }
    }
  }
  
  override def postSuccess(currentState: InprogressForces):InprogressForces = {
    if (currentState.waysToVote == Some(WaysToVote(WaysToVoteType.InPerson)))
        currentState.copy(postalOrProxyVote = 
          Some(PostalOrProxyVote(typeVote = WaysToVoteType.InPerson, 
              postalVoteOption = None, 
              deliveryMethod = None)))
    else currentState
  }
  
  def template(form:InProgressForm[InprogressForces], call:Call, backUrl: Option[Call]): Html = {
    waysToVoteMustache(form.form, call, backUrl.map(_.url))
  }
}
