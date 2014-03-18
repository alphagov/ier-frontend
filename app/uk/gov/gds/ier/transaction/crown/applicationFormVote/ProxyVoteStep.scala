package uk.gov.gds.ier.transaction.crown.applicationFormVote

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{Routes, CrownStep}
import controllers.step.crown.routes._
import scala.Some
import uk.gov.gds.ier.validation.InProgressForm
import uk.gov.gds.ier.model.{InprogressCrown, WaysToVoteType}
import play.api.mvc.Call
import play.api.templates.Html
import controllers.step.crown.ContactController

class ProxyVoteStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)

  extends CrownStep
  with PostalOrProxyVoteForms
  with PostalOrProxyVoteMustache {

  val validation = postalOrProxyVoteForm
  val previousRoute = Some(WaysToVoteController.get)

  val routes = Routes(
    get = ProxyVoteController.get,
    post = ProxyVoteController.post,
    editGet = ProxyVoteController.editGet,
    editPost = ProxyVoteController.editPost
  )

  def template(
      form: InProgressForm[InprogressCrown],
      postEndpoint: Call,
      backEndpoint:Option[Call]): Html = {
    postalOrProxyVoteMustache(form.form, postEndpoint, backEndpoint, WaysToVoteType.ByProxy)
  }

  def nextStep(currentState: InprogressCrown) = {
    ContactController.contactStep
  }

}

