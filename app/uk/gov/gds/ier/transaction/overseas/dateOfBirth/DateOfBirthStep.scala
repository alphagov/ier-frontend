package uk.gov.gds.ier.transaction.overseas.dateOfBirth

import controllers.routes.ExitController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import play.api.mvc.Call
import uk.gov.gds.ier.model.InprogressOverseas
import play.api.templates.Html
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OverseaStep, Routes, GoTo}
import controllers.step.overseas.routes.DateOfBirthController
import controllers.step.overseas.PreviouslyRegisteredController

class DateOfBirthStep @Inject ()(val serialiser: JsonSerialiser,
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
  val previousRoute = None

  def template(form:InProgressForm[InprogressOverseas], postEndpoint:Call, backEndpoint: Option[Call]): Html = {
    dateOfBirthMustache(form.form, postEndpoint, backEndpoint)
  }

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

