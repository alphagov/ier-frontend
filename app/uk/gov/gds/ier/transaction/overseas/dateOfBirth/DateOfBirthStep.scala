package uk.gov.gds.ier.transaction.overseas.dateOfBirth

import controllers.routes.ExitController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OverseaStep, Routes, GoTo}
import controllers.step.overseas.routes.DateOfBirthController
import controllers.step.overseas.PreviouslyRegisteredController
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class DateOfBirthStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)

  extends OverseaStep
  with DateOfBirthForms
  with DateOfBirthMustache {

  val validation = dateOfBirthForm

  val routes = Routes(
    get = DateOfBirthController.get,
    post = DateOfBirthController.post,
    editGet = DateOfBirthController.editGet,
    editPost = DateOfBirthController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    currentState.dob match {
      case Some(dob) if DateValidator.isTooYoungToRegister(dob) => {
        GoTo(ExitController.tooYoung)
      }
      case _ => {
          PreviouslyRegisteredController.previouslyRegisteredStep
      }
    }
  }
}

