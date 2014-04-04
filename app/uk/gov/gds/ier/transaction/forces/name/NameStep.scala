package uk.gov.gds.ier.transaction.forces.name

import controllers.step.forces.NinoController
import controllers.step.forces.routes.{NameController, DateOfBirthController}
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import uk.gov.gds.ier.transaction.forces.InprogressForces

class NameStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)
  extends ForcesStep
  with NameForms
  with NameMustache {

  val validation = nameForm
  val previousRoute = Some(DateOfBirthController.get)

  val routes = Routes(
    get = NameController.get,
    post = NameController.post,
    editGet = NameController.editGet,
    editPost = NameController.editPost
  )

  def template(form: ErrorTransformForm[InprogressForces], call:Call, backUrl: Option[Call]): Html = {
    nameMustache(form, call, backUrl.map(_.url))
  }

  def nextStep(currentState: InprogressForces) = {
    NinoController.ninoStep
  }
}
