package uk.gov.gds.ier.step.confirmation

import controllers._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{JsonSerialiser}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.service.{IerApiService, PlacesService}
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.controller.{ConfirmationStep, OrdinaryController}
import uk.gov.gds.ier.model.InprogressOrdinary

class ConfirmationController @Inject ()(val serialiser: JsonSerialiser,
                                        ierApi: IerApiService,
                                        placesService: PlacesService,
                                        val config: Config,
                                        val encryptionService : EncryptionService,
                                        val encryptionKeys : EncryptionKeys)
  extends ConfirmationStep[InprogressOrdinary]
  with ConfirmationForms {

  def factoryOfT() = InprogressOrdinary()

  val validation = confirmationForm

  def template(form:InProgressForm[InprogressOrdinary]): Html = {
    views.html.steps.confirmation(form)
  }

  def get = ValidSession requiredFor {
    request => application =>
      Ok(template(InProgressForm(validation.fillAndValidate(application))))
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

