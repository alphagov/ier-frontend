package uk.gov.gds.ier.step.openRegister

import controllers._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.controller.StepController
import play.api.data.Form
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressApplication
import play.api.templates.Html

class OpenRegisterController @Inject ()(val serialiser: JsonSerialiser,
                                        val errorTransformer: ErrorTransformer) 
  extends StepController
  with WithSerialiser
  with WithErrorTransformer
  with OpenRegisterForms {

  val validation: Form[InprogressApplication] = openRegisterForm
  val editPostRoute: Call = step.routes.OpenRegisterController.editPost
  val stepPostRoute: Call = step.routes.OpenRegisterController.post

  def template(form:InProgressForm, call:Call): Html = {
    views.html.steps.openRegister(form, call)
  }
  def goToNext(currentState: InprogressApplication): SimpleResult = {
    Redirect(routes.RegisterToVoteController.registerStep("postal-vote"))
  }
}

