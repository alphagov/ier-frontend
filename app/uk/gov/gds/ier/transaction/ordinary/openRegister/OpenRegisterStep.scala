package uk.gov.gds.ier.transaction.ordinary.openRegister

import controllers.step.ordinary.PostalVoteController
import controllers.step.ordinary.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OrdinaryStep, Routes}
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

class OpenRegisterStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService
) extends OrdinaryStep
  with OpenRegisterForms
  with OpenRegisterMustache {

  val validation = openRegisterForm

  val routes = Routes(
    get = OpenRegisterController.get,
    post = OpenRegisterController.post,
    editGet = OpenRegisterController.editGet,
    editPost = OpenRegisterController.editPost
  )

  def nextStep(currentState: InprogressOrdinary) = {
    PostalVoteController.postalVoteStep
  }
  override def isStepComplete(currentState: InprogressOrdinary) = {
    currentState.openRegisterOptin.isDefined
  }
}
