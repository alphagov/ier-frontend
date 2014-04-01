package uk.gov.gds.ier.transaction.ordinary.name

import controllers.step.ordinary.NinoController
import controllers.step.ordinary.routes.{NameController, DateOfBirthController}
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.Call
import uk.gov.gds.ier.model.InprogressOrdinary
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OrdinaryStep, Routes}

class NameStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)
  extends OrdinaryStep
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

  def template(form: ErrorTransformForm[InprogressOrdinary], call:Call, backUrl: Option[Call]): Html = {
    nameMustache(form, call, backUrl.map(_.url))
  }

  def nextStep(currentState: InprogressOrdinary) = {
    NinoController.ninoStep
  }
}
