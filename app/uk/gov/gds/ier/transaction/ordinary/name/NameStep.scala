package uk.gov.gds.ier.transaction.ordinary.name

import controllers.step.ordinary.NinoController
import controllers.step.ordinary.routes.NameController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressOrdinary
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.{OrdinaryStep, Routes}

class NameStep @Inject ()(val serialiser: JsonSerialiser,
                          val config: Config,
                          val encryptionService : EncryptionService,
                          val encryptionKeys : EncryptionKeys)
  extends OrdinaryStep
  with NameForms {

  val validation = nameForm
  val editPostRoute = NameController.editPost
  val stepPostRoute = NameController.post

  val routes = Routes(
    get = NameController.get,
    post = NameController.post,
    edit = NameController.editGet,
    editPost = NameController.editPost
  )

  def template(form:InProgressForm[InprogressOrdinary], call:Call): Html = {
    views.html.steps.name(form, call)
  }
  def nextStep(currentState: InprogressOrdinary) = {
    NinoController.ninoStep
  }
}

