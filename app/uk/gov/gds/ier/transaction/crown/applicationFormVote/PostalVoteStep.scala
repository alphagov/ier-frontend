package uk.gov.gds.ier.transaction.crown.applicationFormVote

import controllers.step.crown.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.CrownStep
import controllers.step.crown.ContactController
import play.api.mvc.Call
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.model.{WaysToVoteType}
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.assets.RemoteAssets

class PostalVoteStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets)

  extends CrownStep
  with PostalOrProxyVoteForms
  with PostalOrProxyVoteMustache {

  val wayToVote = WaysToVoteType.ByPost

  val validation = postalOrProxyVoteForm

  val routing = Routes(
    get = PostalVoteController.get,
    post = PostalVoteController.post,
    editGet = PostalVoteController.editGet,
    editPost = PostalVoteController.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    ContactController.contactStep
  }

}

