package uk.gov.gds.ier.step.confirmation

import play.api.mvc._
import controllers._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.session.SessionHandling
import uk.gov.gds.ier.service.{IerApiService, PlacesService}
import uk.gov.gds.ier.controller.StepController
import play.api.data.Form
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressApplication
import play.api.templates.Html

class ConfirmationController @Inject ()(val serialiser: JsonSerialiser,
                                        errorTransformer: ErrorTransformer,
                                        ierApi: IerApiService,
                                        placesService: PlacesService)
  extends Controller
  with SessionHandling
  with WithSerialiser
  with IerForms {

  val validation: Form[InprogressApplication] = inprogressForm

  def template(form:InProgressForm): Html = {
    views.html.confirmation(form)
  }
  
  def get = ValidSession requiredFor {
    request => application =>
      Ok(template(InProgressForm(validation.fillAndValidate(application))))
  }

  def post = ValidSession requiredFor {
    request => application =>
      validation.fillAndValidate(application).fold(
        hasErrors => {
          val errorsTransformed = errorTransformer.transform(hasErrors)
          Ok(template(InProgressForm(errorsTransformed)))
        },
        validApplication => {
          val refNum = ierApi.generateReferenceNumber(validApplication)
          val remoteClientIP = request.headers.get("X-Real-IP")

          ierApi.submitApplication(remoteClientIP, validApplication, Some(refNum))
          Redirect(routes.CompleteController.complete()).flashing(
            "refNum" -> refNum,
            "postcode" -> validApplication.address.map(_.postcode).getOrElse("")
          )
        }
      )
  }
}

