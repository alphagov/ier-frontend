package uk.gov.gds.ier.transaction.ordinary.confirmation

import controllers.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{JsonSerialiser}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.service.{IerApiService, PlacesService}
import uk.gov.gds.ier.session.SessionHandling
import uk.gov.gds.ier.service.{AddressService, IerApiService, PlacesService}
import play.api.data.Form
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressApplication
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.model.InprogressOrdinary
import uk.gov.gds.ier.step.ConfirmationController

class ConfirmationStep @Inject ()(val serialiser: JsonSerialiser,
                                        ierApi: IerApiService,
                                        addressService: AddressService,
                                        val config: Config,
                                        val encryptionService : EncryptionService,
                                        val encryptionKeys : EncryptionKeys)
  extends ConfirmationController[InprogressOrdinary]
  with ConfirmationForms {

  def factoryOfT() = InprogressOrdinary()

  val validation = confirmationForm

  def template(form:InProgressForm[InprogressOrdinary]): Html = {
    views.html.steps.confirmation(form)
  }

  def get = ValidSession requiredFor {
    request => application =>
      val currentAddressLine = application.address.map { addressService.fillAddressLine(_) }
      val previousAddressLine = application.previousAddress.flatMap { prev =>
        prev.previousAddress.map { addressService.fillAddressLine(_) }
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
          Redirect(CompleteController.complete()).flashing(
            "refNum" -> refNum,
            "postcode" -> validApplication.address.map(_.postcode).getOrElse("")
          )
        }
      )
  }
}

