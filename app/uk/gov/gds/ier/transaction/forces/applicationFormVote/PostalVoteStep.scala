package uk.gov.gds.ier.transaction.forces.applicationFormVote

import controllers.step.forces.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import controllers.step.forces.ContactController
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.model.WaysToVoteType

class PostalVoteStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)

  extends ForcesStep
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

  def nextStep(currentState: InprogressForces) = {
    ContactController.contactStep
  }

}

