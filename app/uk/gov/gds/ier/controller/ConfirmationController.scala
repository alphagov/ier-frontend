package uk.gov.gds.ier.controller

import play.api.mvc._
import controllers._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.session.SessionHandling
import uk.gov.gds.ier.service.{IerApiService, PlacesService}
import play.api.data.Form
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressApplication
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.guice.WithConfig
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}

class ConfirmationController @Inject ()(val serialiser: JsonSerialiser,
                                        ierApi: IerApiService,
                                        placesService: PlacesService,
                                        val config: Config,
                                        val encryptionService : EncryptionService,
                                        val encryptionKeys : EncryptionKeys)
  extends Controller
  with SessionHandling
  with WithSerialiser
  with WithConfig
  with Logging
  with IerForms
  with WithEncryption {

  val validation = inprogressForm

  def template(form:InProgressForm): Html = {
    views.html.confirmation(form)
  }
  
  def get = ValidSession requiredFor {
    request => application =>
      val currentAddressLine = application.address.map { placesService.fillAddressLine(_) }
      val previousAddressLine = application.previousAddress.flatMap { prev => 
        prev.previousAddress.map { placesService.fillAddressLine(_) }
      }
      val appWithAddressLines = application.copy(
        address = currentAddressLine, 
        previousAddress = application.previousAddress.map{ prev =>
          prev.copy(previousAddress = previousAddressLine)
        }
      )

      Ok(template(InProgressForm(validation.fillAndValidate(appWithAddressLines))))
  }

  def post = ValidSession requiredFor {
    request => application =>
      validation.fillAndValidate(application).fold(
        hasErrors => {
          Ok(template(InProgressForm(hasErrors)))
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

