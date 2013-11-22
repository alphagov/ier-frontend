package uk.gov.gds.ier.step.country

import controllers._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.validation.constraints.CountryConstraints
import uk.gov.gds.ier.controller.StepController
import play.api.data.Form
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.{Country, InprogressApplication}
import play.api.templates.Html

class CountryController @Inject ()(val serialiser: JsonSerialiser,
                                   val errorTransformer: ErrorTransformer)
  extends StepController
  with CountryConstraints
  with WithSerialiser
  with WithErrorTransformer
  with CountryForms {

  val validation: Form[InprogressApplication] = countryForm
  val editPostRoute: Call = step.routes.CountryController.editPost
  val stepPostRoute: Call = step.routes.CountryController.post

  def template(form:InProgressForm, call:Call): Html = {
    views.html.steps.country(form, call)
  }
  def goToNext(currentState: InprogressApplication): SimpleResult = {
    currentState.country match {
      case Some(Country("Northern Ireland")) => Redirect(routes.ExitController.northernIreland)
      case Some(Country("Scotland")) => Redirect(routes.ExitController.scotland)
      case _ => Redirect(step.routes.NationalityController.get)
    }
  }
}

