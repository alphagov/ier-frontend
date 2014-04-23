package uk.gov.gds.ier.transaction.crown.applicationFormVote

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{Routes, CrownStep}
import controllers.step.crown.routes._
import scala.Some
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.{WaysToVoteType}
import play.api.mvc.Call
import play.api.templates.Html
import controllers.step.crown.ContactController
import uk.gov.gds.ier.transaction.crown.InprogressCrown

class ProxyVoteStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)

  extends CrownStep
  with PostalOrProxyVoteForms
  with PostalOrProxyVoteMustache {

  val wayToVote = WaysToVoteType.ByProxy

  val validation = postalOrProxyVoteForm

  val routes = Routes(
    get = ProxyVoteController.get,
    post = ProxyVoteController.post,
    editGet = ProxyVoteController.editGet,
    editPost = ProxyVoteController.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    ContactController.contactStep
  }

}

