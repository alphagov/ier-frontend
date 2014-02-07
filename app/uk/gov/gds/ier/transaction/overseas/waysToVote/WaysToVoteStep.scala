package uk.gov.gds.ier.transaction.overseas.waysToVote

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.OverseaStep
import controllers.step.overseas.routes.WaysToVoteController
import controllers.step.overseas.routes.OpenRegisterController
import controllers.step.overseas.PostalVoteController
import uk.gov.gds.ier.step.Routes
import scala.Some
import uk.gov.gds.ier.model.{WaysToVoteType, InprogressOverseas}
import uk.gov.gds.ier.validation.InProgressForm
import play.api.mvc.Call
import play.api.templates.Html
import controllers.step.overseas.ConfirmationController


class WaysToVoteStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val encryptionKeys : EncryptionKeys)
  extends OverseaStep
  with WaysToVoteForms
  with WaysToVoteMustache {

  val validation = waysToVoteForm

  val routes = Routes(
    get = WaysToVoteController.get,
    post = WaysToVoteController.post,
    editGet = WaysToVoteController.editGet,
    editPost = WaysToVoteController.editPost
  )
  val previousRoute = Some(OpenRegisterController.get)

  def nextStep(currentState: InprogressOverseas) = {
    if (currentState.waysToVote.get.waysToVoteType == WaysToVoteType.InPerson) {
      ConfirmationController.confirmationStep
    } else {
      PostalVoteController.postalVoteStep
    }
  }

  def template(form:InProgressForm[InprogressOverseas], call:Call, backUrl: Option[Call]): Html = {
    waysToVoteMustache(form.form, call, backUrl.map(_.url))
  }
}
