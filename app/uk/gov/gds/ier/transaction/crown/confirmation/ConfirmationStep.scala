package uk.gov.gds.ier.transaction.crown.confirmation

import uk.gov.gds.ier.step.ConfirmationStepController
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.validation.ErrorTransformForm
import controllers.step.crown.routes.ConfirmationController
import controllers.step.crown.routes.StatementController
import controllers.routes.CompleteController
import com.google.inject.Inject
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.service.apiservice.IerApiService
import uk.gov.gds.ier.transaction.crown.InprogressCrown


class ConfirmationStep @Inject() (
    val encryptionService: EncryptionService,
    val config: Config,
    val serialiser: JsonSerialiser,
    ierApi: IerApiService)
  extends ConfirmationStepController[InprogressCrown]
    with ConfirmationForms
    with ConfirmationMustache {

  def factoryOfT() = InprogressCrown()

  val routes = Routes(
    get = ConfirmationController.get,
    post = ConfirmationController.post,
    editGet = ConfirmationController.get,
    editPost = ConfirmationController.post
  )

  val validation = confirmationForm
  val previousRoute = Some(StatementController.get)

  def template(form: ErrorTransformForm[InprogressCrown]) = {
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
          val refNum = ierApi.generateCrownReferenceNumber(validApplication)
          val remoteClientIP = request.headers.get("X-Real-IP")

          ierApi.submitCrownApplication(remoteClientIP, validApplication, Some(refNum))
          Redirect(CompleteController.complete()).flashing(
            "refNum" -> refNum,
            "postcode" -> "SW1A 1AA"
          )
        }
      )
  }
}
