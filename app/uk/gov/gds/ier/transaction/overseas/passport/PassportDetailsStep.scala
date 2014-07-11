package uk.gov.gds.ier.transaction.overseas.passport

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import controllers.step.overseas.routes.PassportDetailsController
import controllers.step.overseas.routes.PassportCheckController
import controllers.step.overseas.NameController
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.assets.RemoteAssets

class PassportDetailsStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets
) extends OverseaStep
  with PassportForms
  with PassportDetailsMustache {

  val validation = passportDetailsForm

  val routing = Routes(
    get = PassportDetailsController.get,
    post = PassportDetailsController.post,
    editGet = PassportDetailsController.editGet,
    editPost = PassportDetailsController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    NameController.nameStep
  }
}

