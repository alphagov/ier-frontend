package uk.gov.gds.ier.step.nationality

import controllers.step._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.controller.StepController
import play.api.data.Form
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressApplication
import play.api.templates.Html
import uk.gov.gds.ier.service.IsoCountryService
import uk.gov.gds.ier.guice.WithIsoCountryService

class NationalityController @Inject ()(val serialiser: JsonSerialiser,
                                       val errorTransformer: ErrorTransformer,
                                       val isoCountryService: IsoCountryService)
  extends StepController
  with WithSerialiser
  with WithIsoCountryService
  with WithErrorTransformer
  with NationalityForms {

  val validation: Form[InprogressApplication] = nationalityForm
  val editPostRoute: Call = routes.NationalityController.editPost
  val stepPostRoute: Call = routes.NationalityController.post

  def template(form:InProgressForm, call:Call): Html = {
    views.html.steps.nationality(form, call)
  }
  def goToNext(currentState: InprogressApplication): SimpleResult = {
    Redirect(routes.DateOfBirthController.get)
  }
}

