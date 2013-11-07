package uk.gov.gds.ier.step.previousName

import controllers._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.controller.StepController
import play.api.data.Form
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressApplication
import play.api.templates.Html
import uk.gov.gds.ier.validation._

class PreviousNameController @Inject ()(val serialiser: JsonSerialiser,
                                        val errorTransformer: ErrorTransformer) 
  extends StepController
  with WithSerialiser
  with WithErrorTransformer
  with PreviousNameForms {

  val validation: Form[InprogressApplication] = previousNameForm
  val editPostRoute: Call = step.routes.PreviousNameController.editPost
  val stepPostRoute: Call = step.routes.PreviousNameController.post

  def template(form:InProgressForm, call:Call): Html = {
    views.html.steps.previousName(form, call)
  }

  def goToNext(currentState: InprogressApplication): SimpleResult = {
    Redirect(routes.RegisterToVoteController.registerStep("nino"))
  }
}

