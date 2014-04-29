package uk.gov.gds.ier.transaction.overseas.applicationFormVote

import controllers.step.overseas.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import controllers.step.overseas.ContactController
import uk.gov.gds.ier.model.WaysToVoteType
import scala.Some
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class PostalVoteStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)

  extends OverseaStep
  with PostalOrProxyVoteForms
  with PostalOrProxyVoteMustache {

  val wayToVote = WaysToVoteType.ByPost

  val validation = postalOrProxyVoteForm

  val routes = Routes(
    get = PostalVoteController.get,
    post = PostalVoteController.post,
    editGet = PostalVoteController.editGet,
    editPost = PostalVoteController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    ContactController.contactStep
  }

}

