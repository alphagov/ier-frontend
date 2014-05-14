package uk.gov.gds.ier.transaction.forces.confirmation

import uk.gov.gds.ier.step.ConfirmationStepController
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.validation.ErrorTransformForm
import controllers.step.forces.routes.ConfirmationController
import controllers.step.forces.routes.StatementController
import controllers.routes.CompleteController
import com.google.inject.Inject
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.service.apiservice.IerApiService
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.service.{WithAddressService, AddressService}
import uk.gov.gds.ier.guice.WithRemoteAssets
import uk.gov.gds.ier.assets.RemoteAssets


class ConfirmationStep @Inject() (
    val encryptionService: EncryptionService,
    val config: Config,
    val serialiser: JsonSerialiser,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    ierApi: IerApiService)
  extends ConfirmationStepController[InprogressForces]
    with ConfirmationForms
    with ConfirmationMustache
    with WithAddressService
    with WithRemoteAssets {

  def factoryOfT() = InprogressForces()



  val routes = Routes(
    get = ConfirmationController.get,
    post = ConfirmationController.post,
    editGet = ConfirmationController.get,
    editPost = ConfirmationController.post
  )

  val validation = confirmationForm

  def template(form: ErrorTransformForm[InprogressForces]) = {
    Confirmation.confirmationPage(
      form,
      routes.post.url
    )
  }

  def get = ValidSession requiredFor {
    request => application =>
      Ok(template(validation.fillAndValidate(application)))
  }

  def post = ValidSession requiredFor {
    implicit request => application =>
      validation.fillAndValidate(application).fold(
        hasErrors => {
          Ok(template(hasErrors))
        },
        validApplication => {
          val refNum = ierApi.generateForcesReferenceNumber(validApplication)
          val remoteClientIP = request.headers.get("X-Real-IP")

          ierApi.submitForcesApplication(remoteClientIP, validApplication, Some(refNum))

          logSession()

          Redirect(CompleteController.complete()).flashing(
            "refNum" -> refNum,
            "postcode" -> "SW1A 1AA"
          )
        }
      )
  }
}
