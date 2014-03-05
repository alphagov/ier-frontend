package uk.gov.gds.ier.transaction.forces.openRegister

import controllers.step.forces.PostalVoteController
import controllers.step.forces.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.Call
import uk.gov.gds.ier.model.InprogressForces
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.{ForcesStep, Routes}

class OpenRegisterStep @Inject ()(val serialiser: JsonSerialiser,
                                        val config: Config,
                                        val encryptionService : EncryptionService,
                                        val encryptionKeys : EncryptionKeys)
  extends ForcesStep
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

  def template(form:InProgressForm[InprogressForces], call:Call, backUrl: Option[Call]): Html = {
    openRegisterMustache(form.form, call, backUrl)
  }
  def nextStep(currentState: InprogressForces) = {
    PostalVoteController.postalVoteStep
  }
  override def isStepComplete(currentState: InprogressForces) = {
    currentState.openRegisterOptin.isDefined
  }
}
