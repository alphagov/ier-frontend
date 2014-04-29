package uk.gov.gds.ier.transaction.forces.statement

import controllers.step.forces.AddressController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.model._
import play.api.templates.Html
import controllers.step.forces.routes._

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import play.api.mvc.Call
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.forces.InprogressForces

class StatementStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)
  extends ForcesStep
    with StatementForms
    with StatementMustache {

  val validation = statementForm

  val routes = Routes(
    get = StatementController.get,
    post = StatementController.post,
    editGet = StatementController.editGet,
    editPost = StatementController.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    AddressController.addressStep
  }
}
