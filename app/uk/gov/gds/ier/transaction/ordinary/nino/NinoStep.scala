package uk.gov.gds.ier.transaction.ordinary.nino

import controllers.step.ordinary.routes.NinoController
import controllers.step.ordinary.AddressController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.{InprogressOrdinary, InprogressApplication}
import play.api.templates.Html

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.{OrdinaryStep, Routes}

class NinoStep @Inject ()(val serialiser: JsonSerialiser,
                          val config: Config,
                          val encryptionService : EncryptionService,
                          val encryptionKeys : EncryptionKeys)
  extends OrdinaryStep
  with NinoForms {

  val validation = ninoForm
  val editPostRoute = NinoController.editPost
  val stepPostRoute = NinoController.post

  val routes = Routes(
    get = NinoController.get,
    post = NinoController.post,
    edit = NinoController.editGet,
    editPost = NinoController.editPost
  )

  def template(form:InProgressForm[InprogressOrdinary], call:Call): Html = {
    views.html.steps.nino(form, call)
  }
  def nextStep(currentState: InprogressOrdinary) = {
    AddressController.addressStep
  }
}

