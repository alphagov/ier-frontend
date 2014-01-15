package uk.gov.gds.ier.transaction.ordinary.openRegister

import controllers.step.ordinary.PostalVoteController
import controllers.step.ordinary.routes.OpenRegisterController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressOrdinary
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.{OrdinaryStep, Routes}

class OpenRegisterStep @Inject ()(val serialiser: JsonSerialiser,
                                        val config: Config,
                                        val encryptionService : EncryptionService,
                                        val encryptionKeys : EncryptionKeys)
  extends OrdinaryStep
  with OpenRegisterForms {

  val validation = openRegisterForm
  val editPostRoute = OpenRegisterController.editPost
  val stepPostRoute = OpenRegisterController.post

  val routes = Routes(
    get = OpenRegisterController.get,
    post = OpenRegisterController.post,
    edit = OpenRegisterController.editGet,
    editPost = OpenRegisterController.editPost
  )

  def template(form:InProgressForm[InprogressOrdinary], call:Call): Html = {
    views.html.steps.openRegister(form, call)
  }
  def nextStep(currentState: InprogressOrdinary) = {
    PostalVoteController.postalVoteStep
  }
  override def isStepComplete(currentState: InprogressOrdinary) = {
    currentState.openRegisterOptin.isDefined
  }
}

