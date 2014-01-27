package uk.gov.gds.ier.transaction.overseas.dateOfBirth

import controllers.routes.ExitController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.{InprogressOverseas, DateOfBirth, noDOB}
import play.api.templates.Html
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.validation.constants.DateOfBirthConstants
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.{OverseaStep, OrdinaryStep, Routes, Exit}
import controllers.step.routes.CountryController
import controllers.step.overseas.routes.DateOfBirthController
import controllers.step.overseas.{PreviouslyRegisteredController}
import uk.gov.gds.ier.model.DOB
import controllers.step.overseas.FirstTimeRegisteredController

class DateOfBirthStep @Inject ()(val serialiser: JsonSerialiser,
                                       val config: Config,
                                       val encryptionService : EncryptionService,
                                       val encryptionKeys : EncryptionKeys)
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
  val previousRoute = Some(CountryController.get)

  def template(form:InProgressForm[InprogressOverseas], postEndpoint:Call, backEndpoint: Option[Call]): Html = {
    dateOfBirthMustache(form.form, postEndpoint, backEndpoint)
  }

  def nextStep(currentState: InprogressOverseas) = {
    currentState.dob match {
      case Some(dob) if DateValidator.isTooYoungToRegister(dob) => {
          println ("dob => " + dob)
        Exit(ExitController.tooYoung)
      }
      case _ => {
          println ("first time")
          FirstTimeRegisteredController.firstTimeStep    
      }
    }
  }
}

