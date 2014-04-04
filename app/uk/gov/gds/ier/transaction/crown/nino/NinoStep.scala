package uk.gov.gds.ier.transaction.crown.nino

import controllers.step.crown.routes.{NinoController, JobController}
import controllers.step.crown.ContactAddressController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.Call
import play.api.templates.Html

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{CrownStep, Routes}
import uk.gov.gds.ier.transaction.crown.InprogressCrown

class NinoStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)
  extends CrownStep
  with NinoForms
  with NinoMustache {

  val validation = ninoForm
  val previousRoute = Some(JobController.get)

  val routes = Routes(
    get = NinoController.get,
    post = NinoController.post,
    editGet = NinoController.editGet,
    editPost = NinoController.editPost
  )

  def template(
      form: ErrorTransformForm[InprogressCrown],
      postEndpoint: Call,
      backEndpoint:Option[Call]): Html = {
    ninoMustache(form, postEndpoint, backEndpoint)
  }
  def nextStep(currentState: InprogressCrown) = {
    ContactAddressController.contactAddressStep
  }
}

