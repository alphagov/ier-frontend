package uk.gov.gds.ier.step.dateOfBirth

import controllers.step._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.controller.StepController
import play.api.data.Form
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressApplication
import play.api.templates.Html
import uk.gov.gds.ier.validation._

class DateOfBirthController @Inject ()(val serialiser: JsonSerialiser,
                                       val errorTransformer: ErrorTransformer) 
  extends StepController
  with WithSerialiser
  with WithErrorTransformer
  with DateOfBirthForms {

  val validation: Form[InprogressApplication] = dateOfBirthForm
  val editPostRoute: Call = routes.DateOfBirthController.editPost
  val stepPostRoute: Call = routes.DateOfBirthController.post

  def template(form:InProgressForm, call:Call): Html = {
    views.html.steps.dateOfBirth(form, call)
  }

  def goToNext(currentState: InprogressApplication): SimpleResult = {
    Redirect(routes.NameController.get)
  }
}

