package uk.gov.gds.ier.transaction.forces.nino

import controllers.step.forces.routes.{NinoController, NameController}
import controllers.step.forces.ServiceController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.Call
import uk.gov.gds.ier.model.InprogressForces
import play.api.templates.Html

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{ForcesStep, Routes}

class NinoStep @Inject ()(val serialiser: JsonSerialiser,
                          val config: Config,
                          val encryptionService : EncryptionService)
  extends ForcesStep
  with NinoForms
  with NinoMustache {

  val validation = ninoForm
  val previousRoute = Some(NameController.get)

  val routes = Routes(
    get = NinoController.get,
    post = NinoController.post,
    editGet = NinoController.editGet,
    editPost = NinoController.editPost
  )

  def template(
      form: ErrorTransformForm[InprogressForces],
      postEndpoint: Call,
      backEndpoint:Option[Call]): Html = {
    ninoMustache(form, postEndpoint, backEndpoint)
  }
  def nextStep(currentState: InprogressForces) = {
    ServiceController.serviceStep
  }
}

