package uk.gov.gds.ier.step.dateOfBirth

import controllers.step._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.controller.StepController
import play.api.data.Form
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.{DateOfBirth, InprogressApplication}
import play.api.templates.Html
import uk.gov.gds.ier.validation._

class DateOfBirthController @Inject ()(val serialiser: JsonSerialiser)
  extends StepController
  with WithSerialiser
  with DateOfBirthForms {

  val validation = dateOfBirthForm
  val editPostRoute = routes.DateOfBirthController.editPost
  val stepPostRoute = routes.DateOfBirthController.post

  def template(form:InProgressForm, call:Call): Html = {
    views.html.steps.dateOfBirth(form, call)
  }

  def goToNext(currentState: InprogressApplication): SimpleResult = {
    currentState.dob match {
      case Some(DateOfBirth(Some(dob), _)) if DateValidator.isTooYoungToRegister(dob) => {
        Redirect(controllers.routes.ExitController.tooYoung)
      }
      case Some(DateOfBirth(_, Some(noDob))) if noDob.range == DateOfBirthConstants.under18 => {
        Redirect(controllers.routes.ExitController.under18)
      }
      case Some(DateOfBirth(_, Some(noDob))) if noDob.range == DateOfBirthConstants.dontKnow => {
        Redirect(controllers.routes.ExitController.dontKnow)
      }
      case _ => Redirect(routes.NameController.get)
    }
  }
}

