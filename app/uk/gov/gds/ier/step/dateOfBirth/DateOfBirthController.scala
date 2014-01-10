package uk.gov.gds.ier.step.dateOfBirth

import controllers.step._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.controller.OrdinaryController
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.{InprogressOrdinary, DateOfBirth, InprogressApplication, noDOB}
import play.api.templates.Html
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.validation.constants.DateOfBirthConstants

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}

class DateOfBirthController @Inject ()(val serialiser: JsonSerialiser,
                                       val config: Config,
                                       val encryptionService : EncryptionService,
                                       val encryptionKeys : EncryptionKeys)
  extends OrdinaryController
  with DateOfBirthForms {

  val validation = dateOfBirthForm
  val editPostRoute = routes.DateOfBirthController.editPost
  val stepPostRoute = routes.DateOfBirthController.post

  def template(form:InProgressForm[InprogressOrdinary], call:Call): Html = {
    views.html.steps.dateOfBirth(form, call)
  }

  def goToNext(currentState: InprogressOrdinary): SimpleResult = {
    currentState.dob match {
      case Some(DateOfBirth(Some(dob), _)) if DateValidator.isTooYoungToRegister(dob) => {
        Redirect(controllers.routes.ExitController.tooYoung)
      }
      case Some(DateOfBirth(_, Some(noDOB(Some(reason), Some(range))))) if range == DateOfBirthConstants.under18 => {
        Redirect(controllers.routes.ExitController.under18)
      }
      case Some(DateOfBirth(_, Some(noDOB(Some(reason), Some(range))))) if range == DateOfBirthConstants.dontKnow => {
        Redirect(controllers.routes.ExitController.dontKnow)
      }
      case _ => Redirect(routes.NameController.get)
    }
  }
}

