package uk.gov.gds.ier.transaction.ordinary.name

import controllers.step.ordinary.NinoController
import controllers.step.ordinary.routes.{NameController, DateOfBirthController}
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OrdinaryStepWithNewMustache, Routes}
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

class NameStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)
  extends OrdinaryStepWithNewMustache
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

  def nextStep(currentState: InprogressOrdinary) = {
    NinoController.ninoStep
  }
}
