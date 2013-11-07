package uk.gov.gds.ier.step.name

import controllers._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.controller.StepController
import play.api.data.Form
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressApplication
import play.api.templates.Html

class NameController @Inject ()(val serialiser: JsonSerialiser,
                                val errorTransformer: ErrorTransformer) extends StepController
                                                                        with WithSerialiser
                                                                        with WithErrorTransformer
                                                                        with NameForms {

  val validation: Form[InprogressApplication] = nameForm
  val editPostRoute: Call = step.routes.NameController.editPost
  val stepPostRoute: Call = step.routes.NameController.post

  def template(form:InProgressForm, call:Call): Html = {
    views.html.steps.name(form, call)
  }
  def goToNext(currentState: InprogressApplication): SimpleResult = {
    Redirect(routes.RegisterToVoteController.registerStep("previous-name"))
  }
}

