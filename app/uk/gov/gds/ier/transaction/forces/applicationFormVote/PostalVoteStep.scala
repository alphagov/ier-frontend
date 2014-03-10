package uk.gov.gds.ier.transaction.forces.applicationFormVote

import controllers.step.forces.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.ForcesStep
import controllers.step.forces.ContactController
import play.api.mvc.Call
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.model.{InprogressForces, WaysToVoteType}
import uk.gov.gds.ier.validation.InProgressForm
import play.api.mvc.SimpleResult

class PostalVoteStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val encryptionKeys : EncryptionKeys)

  extends ForcesStep
  with PostalOrProxyVoteForms
  with PostalOrProxyVoteMustache {

  val validation = postalOrProxyVoteForm
  val previousRoute = Some(WaysToVoteController.get)

  val routes = Routes(
    get = PostalVoteController.get,
    post = PostalVoteController.post,
    editGet = PostalVoteController.editGet,
    editPost = PostalVoteController.editPost
  )

  def template(form: InProgressForm[InprogressForces], postEndpoint: Call, backEndpoint:Option[Call]): Html = {
    postalOrProxyVoteMustache(form.form, postEndpoint, backEndpoint, WaysToVoteType.ByPost)
  }

  def nextStep(currentState: InprogressForces) = {
    ContactController.contactStep
  }

}

