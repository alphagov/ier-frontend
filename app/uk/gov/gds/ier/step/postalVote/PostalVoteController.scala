package uk.gov.gds.ier.step.postalVote

import controllers.step._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.controller.OrdinaryController
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.{InprogressOrdinary, InprogressApplication}
import play.api.templates.Html

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}

class PostalVoteController @Inject ()(val serialiser: JsonSerialiser,
                                      val config: Config,
                                      val encryptionService : EncryptionService,
                                      val encryptionKeys : EncryptionKeys)
  extends OrdinaryController
  with PostalVoteForms {

  val validation = postalVoteForm
  val editPostRoute = routes.PostalVoteController.editPost
  val stepPostRoute = routes.PostalVoteController.post

  def template(form:InProgressForm[InprogressOrdinary], call:Call): Html = {
    views.html.steps.postalVote(form, call)
  }
  def goToNext(currentState: InprogressOrdinary): SimpleResult = {
    Redirect(routes.ContactController.get)
  }
}

