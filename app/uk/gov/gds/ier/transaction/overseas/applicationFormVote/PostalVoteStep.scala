package uk.gov.gds.ier.transaction.overseas.applicationFormVote

import controllers.step.overseas.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import play.api.templates.Html

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.OverseaStep
import controllers.step.overseas.ContactController
import play.api.mvc.Call
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.model.{InprogressOverseas, WaysToVoteType}
import uk.gov.gds.ier.validation.InProgressForm
import scala.Some

class PostalVoteStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)

  extends OverseaStep
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

  def template(form: InProgressForm[InprogressOverseas], postEndpoint: Call, backEndpoint:Option[Call]): Html = {
    postalOrProxyVoteMustache(form.form, postEndpoint, backEndpoint, WaysToVoteType.ByPost)
  }

  def nextStep(currentState: InprogressOverseas) = {
    ContactController.contactStep
  }

}

