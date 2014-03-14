package uk.gov.gds.ier.transaction.crown.openRegister

import controllers.step.crown.WaysToVoteController
import controllers.step.crown.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.Call
import uk.gov.gds.ier.model.InprogressCrown
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{CrownStep, Routes}

class OpenRegisterStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)
  extends CrownStep
  with OpenRegisterForms 
  with OpenRegisterMustache {

  val validation = openRegisterForm
  val previousRoute = Some(ContactAddressController.get)

  val routes = Routes(
    get = OpenRegisterController.get,
    post = OpenRegisterController.post,
    editGet = OpenRegisterController.editGet,
    editPost = OpenRegisterController.editPost
  )

  def template(form:InProgressForm[InprogressCrown], call:Call, backUrl: Option[Call]): Html = {
    openRegisterMustache(form.form, call, backUrl)
  }
  def nextStep(currentState: InprogressCrown) = {
    WaysToVoteController.waysToVoteStep
  }
  override def isStepComplete(currentState: InprogressCrown) = {
    currentState.openRegisterOptin.isDefined
  }
}
