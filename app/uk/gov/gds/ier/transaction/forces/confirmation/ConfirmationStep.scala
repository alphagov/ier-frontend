package uk.gov.gds.ier.transaction.forces.confirmation

import uk.gov.gds.ier.model.InprogressForces
import uk.gov.gds.ier.step.ConfirmationStepController
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.IerApiService
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.validation.ErrorTransformForm
import controllers.step.forces.routes.ConfirmationController
import controllers.step.forces.routes.StatementController
import controllers.routes.CompleteController
import com.google.inject.Inject
import uk.gov.gds.ier.step.Routes


class ConfirmationStep @Inject() (
    val encryptionService: EncryptionService,
    val config: Config,
    val serialiser: JsonSerialiser,
    ierApi: IerApiService)
  extends ConfirmationStepController[InprogressForces]
    with ConfirmationForms
    with ConfirmationMustache {

  def factoryOfT() = InprogressForces()

  val routes = Routes(
    get = ConfirmationController.get,
    post = ConfirmationController.post,
    editGet = ConfirmationController.get,
    editPost = ConfirmationController.post
  )

  val validation = confirmationForm
  val previousRoute = Some(StatementController.get)

  def template(form: ErrorTransformForm[InprogressForces]) = {
    Confirmation.confirmationPage(
      form,
      previousRoute.map(_.url).getOrElse("#"),
      routes.post.url
    )
  }

  def get = ValidSession requiredFor {
    request => application =>
      Ok(template(validation.fillAndValidate(application)))
  }

  def post = ValidSession requiredFor {
    request => application =>
      validation.fillAndValidate(application).fold(
        hasErrors => {
          Ok(template(hasErrors))
        },
        validApplication => {
          val refNum = ierApi.generateForcesReferenceNumber(validApplication)
          val remoteClientIP = request.headers.get("X-Real-IP")

          ierApi.submitForcesApplication(remoteClientIP, validApplication, Some(refNum))
          Redirect(CompleteController.complete()).flashing(
            "refNum" -> refNum,
            "postcode" -> "SW1A 1AA"
          )
        }
      )
  }
}
