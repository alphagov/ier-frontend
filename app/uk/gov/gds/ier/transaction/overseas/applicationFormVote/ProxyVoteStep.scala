package uk.gov.gds.ier.transaction.overseas.applicationFormVote

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.{Routes, OverseaStep}
import controllers.step.overseas.routes._
import scala.Some
import uk.gov.gds.ier.validation.InProgressForm
import uk.gov.gds.ier.model.InprogressOverseas
import play.api.mvc.Call
import play.api.templates.Html
import controllers.step.overseas.ContactController

class ProxyVoteStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val encryptionKeys : EncryptionKeys)

  extends OverseaStep
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

  def template(form: InProgressForm[InprogressOverseas], postEndpoint: Call, backEndpoint:Option[Call]): Html = {
    postalOrProxyVoteMustache(form.form, postEndpoint, backEndpoint, "proxy")
  }

  def nextStep(currentState: InprogressOverseas) = {
    ContactController.contactStep
  }

}

