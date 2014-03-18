package uk.gov.gds.ier.transaction.crown.dateOfBirth

import controllers.step.crown.NameController
import controllers.step.crown.routes.{DateOfBirthController, NationalityController}
import controllers.routes.ExitController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import play.api.mvc.Call
import uk.gov.gds.ier.model.{InprogressCrown, DateOfBirth, noDOB}
import play.api.templates.Html
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.validation.constants.DateOfBirthConstants
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{CrownStep, Routes, Exit}

class DateOfBirthStep @Inject ()(val serialiser: JsonSerialiser,
                                       val config: Config,
                                       val encryptionService : EncryptionService)
  extends CrownStep
  with DateOfBirthForms
  with DateOfBirthMustache{

  val validation = dateOfBirthForm
  val previousRoute = Some(NationalityController.get)

  val routes = Routes(
    get = DateOfBirthController.get,
    post = DateOfBirthController.post,
    editGet = DateOfBirthController.editGet,
    editPost = DateOfBirthController.editPost
  ) 

  def template(
      form:InProgressForm[InprogressCrown],
      postEndpoint:Call,
      backEndpoint: Option[Call]): Html = {
    dateOfBirthMustache(form.form, postEndpoint, backEndpoint)
  }

  def nextStep(currentState: InprogressCrown) = {
    currentState.dob match {
      case Some(DateOfBirth(Some(dob), _)) if DateValidator.isTooYoungToRegister(dob) => {
        Exit(ExitController.tooYoung)
      }
      case Some(DateOfBirth(_, Some(noDOB(Some(reason), Some(range))))) 
        if range == DateOfBirthConstants.under18 => {
          Exit(ExitController.under18)
      }
      case Some(DateOfBirth(_, Some(noDOB(Some(reason), Some(range))))) 
        if range == DateOfBirthConstants.dontKnow => {
          Exit(ExitController.dontKnow)
      }
      case _ => NameController.nameStep    
    }
  }
}

