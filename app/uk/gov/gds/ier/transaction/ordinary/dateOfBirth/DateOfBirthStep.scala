package uk.gov.gds.ier.transaction.ordinary.dateOfBirth

import controllers.step.ordinary.NameController
import controllers.step.ordinary.routes.DateOfBirthController
import controllers.routes.ExitController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.{InprogressOrdinary, DateOfBirth, noDOB}
import play.api.templates.Html
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.validation.constants.DateOfBirthConstants
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.{OrdinaryStep, Routes, Exit}
import uk.gov.gds.ier.transaction.ordinary.name.NameStep

class DateOfBirthStep @Inject ()(val serialiser: JsonSerialiser,
                                       val config: Config,
                                       val encryptionService : EncryptionService,
                                       val encryptionKeys : EncryptionKeys)
  extends OrdinaryStep
  with DateOfBirthForms {

  val validation = dateOfBirthForm
  val editPostRoute = DateOfBirthController.editPost
  val stepPostRoute = DateOfBirthController.post

  val routes = Routes(
    get = DateOfBirthController.get,
    post = DateOfBirthController.post,
    edit = DateOfBirthController.editGet,
    editPost = DateOfBirthController.editPost
  ) 

  def template(form:InProgressForm[InprogressOrdinary], call:Call): Html = {
    views.html.steps.dateOfBirth(form, call)
  }

  def nextStep(currentState: InprogressOrdinary) = {
    currentState.dob match {
      case Some(DateOfBirth(Some(dob), _)) if DateValidator.isTooYoungToRegister(dob) => {
        Exit(ExitController.tooYoung)
      }
      case Some(DateOfBirth(_, Some(noDOB(Some(reason), Some(range))))) if range == DateOfBirthConstants.under18 => {
        Exit(ExitController.under18)
      }
      case Some(DateOfBirth(_, Some(noDOB(Some(reason), Some(range))))) if range == DateOfBirthConstants.dontKnow => {
        Exit(ExitController.dontKnow)
      }
      case _ => NameController.nameStep    
    }
  }
}

