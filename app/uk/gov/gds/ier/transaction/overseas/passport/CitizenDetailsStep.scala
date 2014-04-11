package uk.gov.gds.ier.transaction.overseas.passport

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import controllers.step.overseas.routes.PassportCheckController
import controllers.step.overseas.routes.CitizenDetailsController
import controllers.step.overseas.NameController
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class CitizenDetailsStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)
  extends OverseaStep
  with PassportForms
  with CitizenDetailsMustache {

  val validation = citizenDetailsForm
  val previousRoute = Some(PassportCheckController.get)

  val routes = Routes(
    get = CitizenDetailsController.get,
    post = CitizenDetailsController.post,
    editGet = CitizenDetailsController.editGet,
    editPost = CitizenDetailsController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    NameController.nameStep
  }
}

