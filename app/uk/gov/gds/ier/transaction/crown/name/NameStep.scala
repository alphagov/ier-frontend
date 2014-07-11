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
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.step.{CrownStep, Routes}
import uk.gov.gds.ier.assets.RemoteAssets

class NameStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets)
  extends CrownStep
  with NameForms
  with NameMustache {

  val validation = nameForm

  val routing = Routes(
    get = NameController.get,
    post = NameController.post,
    editGet = NameController.editGet,
    editPost = NameController.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    JobController.jobStep
  }
}
