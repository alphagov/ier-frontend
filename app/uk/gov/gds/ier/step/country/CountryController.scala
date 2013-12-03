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
import uk.gov.gds.ier.guice.WithConfig
import uk.gov.gds.ier.config.Config

class CountryController @Inject ()(val serialiser: JsonSerialiser,
                                   val config:Config)
  extends StepController
  with CountryConstraints
  with WithSerialiser
  with WithConfig
  with CountryForms {

  val validation = countryForm
  val editPostRoute = step.routes.CountryController.editPost
  val stepPostRoute = step.routes.CountryController.post

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

