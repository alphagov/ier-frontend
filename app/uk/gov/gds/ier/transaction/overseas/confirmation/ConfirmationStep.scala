package uk.gov.gds.ier.transaction.overseas.confirmation

import uk.gov.gds.ier.model.{ApplicationType}
import uk.gov.gds.ier.step.ConfirmationStepController
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.validation.ErrorTransformForm
import controllers.step.overseas.routes.ConfirmationController
import controllers.routes.CompleteController
import com.google.inject.Inject
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.mustache.ErrorPageMustache
import uk.gov.gds.ier.service.apiservice.IerApiService
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.guice.WithRemoteAssets

class ConfirmationStep @Inject() (
    val encryptionService: EncryptionService,
    val config: Config,
    val serialiser: JsonSerialiser,
    ierApi: IerApiService,
    val remoteAssets: RemoteAssets
) extends ConfirmationStepController[InprogressOverseas]
  with ConfirmationForms
  with ErrorPageMustache
  with ConfirmationMustache
  with WithRemoteAssets {

  def factoryOfT() = InprogressOverseas()

  val routes = Routes(
    get = ConfirmationController.get,
    post = ConfirmationController.post,
    editGet = ConfirmationController.get,
    editPost = ConfirmationController.post
  )

  val validation = confirmationForm

  def template(form: ErrorTransformForm[InprogressOverseas]) = {
    Confirmation.confirmationPage(
      form,
      routes.post.url
    )
  }

  def get = ValidSession requiredFor {
    request => application =>
      application.identifyApplication match {
        case ApplicationType.DontKnow => NotFound(ErrorPage.NotFound(request.path))
        case _ => Ok(template(validation.fillAndValidate(application)))
      }
  }

  def post = ValidSession requiredFor {
    implicit request => application =>
      validation.fillAndValidate(application).fold(
        hasErrors => {
          Ok(template(hasErrors))
        },
        validApplication => {
          val refNum = ierApi.generateOverseasReferenceNumber(validApplication)
          val remoteClientIP = request.headers.get("X-Real-IP")

          val response = ierApi.submitOverseasApplication(remoteClientIP, validApplication, Some(refNum))

          logSession()

          Redirect(CompleteController.complete()).flashing(
            "refNum" -> refNum,
            "localAuthority" -> serialiser.toJson(response.localAuthority)
          )
        }
      )
  }
}
