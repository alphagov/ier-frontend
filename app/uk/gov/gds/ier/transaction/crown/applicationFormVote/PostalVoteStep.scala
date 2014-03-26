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
import uk.gov.gds.ier.model.{InprogressCrown, WaysToVoteType}
import uk.gov.gds.ier.validation.ErrorTransformForm

class PostalVoteStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)

  extends CrownStep
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

  def template(
      form: ErrorTransformForm[InprogressCrown],
      postEndpoint: Call,
      backEndpoint:Option[Call]): Html = {
    postalOrProxyVoteMustache(form, postEndpoint, backEndpoint, WaysToVoteType.ByPost)
  }

  def nextStep(currentState: InprogressCrown) = {
    ContactController.contactStep
  }

}

