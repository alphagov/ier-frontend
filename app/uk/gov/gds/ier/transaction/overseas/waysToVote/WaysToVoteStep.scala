package uk.gov.gds.ier.transaction.overseas.waysToVote

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.{Routes, OverseaStep}
import controllers.step.overseas.routes.{RegisteredAddressController, WaysToVoteController}
import controllers.step.overseas.PostalVoteController
import uk.gov.gds.ier.step.Routes
import scala.Some
import uk.gov.gds.ier.model.InprogressOverseas
import uk.gov.gds.ier.validation.InProgressForm
import play.api.mvc.Call
import play.api.templates.Html


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
  val previousRoute = Some(RegisteredAddressController.get)

  def nextStep(currentState: InprogressOverseas) = {
    // FIXME: unfinished!
    // postal vote is selected only when user select posta vote, otherwise skip postal vote and
    // go straight to ContactController.get
    PostalVoteController.postalVoteStep
    // ContactController.get
  }

  def template(form:InProgressForm[InprogressOverseas], call:Call, backUrl: Option[Call]): Html = {
    waysToVoteMustache(form.form, call, backUrl.map(_.url))
  }
}
