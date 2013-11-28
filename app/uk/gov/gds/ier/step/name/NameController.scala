package uk.gov.gds.ier.step.name

import controllers.step._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.controller.StepController
import play.api.data.Form
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressApplication
import play.api.templates.Html

class NameController @Inject ()(val serialiser: JsonSerialiser)
  extends StepController
  with WithSerialiser
  with NameForms {

  val validation = nameForm
  val editPostRoute = routes.NameController.editPost
  val stepPostRoute = routes.NameController.post

  def template(form:InProgressForm, call:Call): Html = {
    views.html.steps.name(form, call)
  }
  def goToNext(currentState: InprogressApplication): SimpleResult = {
    Redirect(routes.NinoController.get)
  }
}

