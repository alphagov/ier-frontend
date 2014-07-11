package uk.gov.gds.ier.transaction.overseas.name

import com.google.inject.Inject
import controllers.step.overseas.NinoController
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import controllers.step.overseas.routes.NameController
import controllers.step.overseas.routes.LastUkAddressController
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.assets.RemoteAssets

class NameStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets
) extends OverseaStep
  with NameForms
  with NameMustache {

  val validation = nameForm

  val routing = Routes(
    get = NameController.get,
    post = NameController.post,
    editGet = NameController.editGet,
    editPost = NameController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    NinoController.ninoStep
  }
}
