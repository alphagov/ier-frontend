package uk.gov.gds.ier.transaction.crown.name

import controllers.step.crown.JobController
import controllers.step.crown.routes.{NameController, DateOfBirthController}
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{CrownStep, Routes}
import uk.gov.gds.ier.transaction.crown.InprogressCrown

class NameStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)
  extends CrownStep
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

  def template(form: ErrorTransformForm[InprogressCrown], call:Call, backUrl: Option[Call]): Html = {
    nameMustache(form, call, backUrl.map(_.url))
  }

  def nextStep(currentState: InprogressCrown) = {
    JobController.jobStep
  }
}
