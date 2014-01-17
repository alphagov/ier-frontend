package uk.gov.gds.ier.transaction.ordinary.postalVote

import controllers.step.ordinary.ContactController
import controllers.step.ordinary.routes.{PostalVoteController, OpenRegisterController}
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.{Contact, InprogressOrdinary, InprogressApplication}
import play.api.templates.Html

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.{OrdinaryStep, Routes}

class PostalVoteStep @Inject ()(val serialiser: JsonSerialiser,
                                      val config: Config,
                                      val encryptionService : EncryptionService,
                                      val encryptionKeys : EncryptionKeys)
  extends OrdinaryStep
  with PostalVoteForms {

  val validation = postalVoteForm
  val previousRoute = Some(OpenRegisterController.get)

  val routes = Routes(
    get = PostalVoteController.get,
    post = PostalVoteController.post,
    editGet = PostalVoteController.editGet,
    editPost = PostalVoteController.editPost
  )

  def template(form:InProgressForm[InprogressOrdinary], call:Call, backUrl: Option[Call]): Html = {
    views.html.steps.postalVote(form, call, backUrl.map(_.url))
  }
  def nextStep(currentState: InprogressOrdinary) = {
    ContactController.contactStep
  }

}

