package uk.gov.gds.ier.transaction.overseas.postalVote

import controllers.step.overseas.routes.{PostalVoteController, OpenRegisterController}
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.model._
import play.api.templates.Html

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import play.api.mvc.Call
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.validation.InProgressForm
import scala.Some
import controllers.step.overseas.ContactController

class PostalVoteStep @Inject ()(val serialiser: JsonSerialiser,
                                      val config: Config,
                                      val encryptionService : EncryptionService,
                                      val encryptionKeys : EncryptionKeys)
  extends OverseaStep
  with PostalVoteForms
  with PostalVoteMustache {

  val validation = postalVoteForm
  val previousRoute = Some(OpenRegisterController.get)

  val routes = Routes(
    get = PostalVoteController.get,
    post = PostalVoteController.post,
    editGet = PostalVoteController.editGet,
    editPost = PostalVoteController.editPost
  )

  def template(form: InProgressForm[InprogressOverseas], postEndpoint: Call, backEndpoint:Option[Call]): Html = {
    postalVoteMustache(form.form, postEndpoint, backEndpoint)
  }

  def nextStep(currentState: InprogressOverseas) = {
    ContactController.contactStep
  }

}

