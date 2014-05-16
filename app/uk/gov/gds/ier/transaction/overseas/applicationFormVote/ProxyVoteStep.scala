package uk.gov.gds.ier.transaction.overseas.applicationFormVote

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import controllers.step.overseas.routes._
import scala.Some
import uk.gov.gds.ier.model.WaysToVoteType
import controllers.step.overseas.ContactController
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.assets.RemoteAssets

class ProxyVoteStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets)

  extends OverseaStep
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

  def nextStep(currentState: InprogressOverseas) = {
    ContactController.contactStep
  }

}

