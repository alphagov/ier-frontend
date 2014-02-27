package uk.gov.gds.ier.transaction.overseas.confirmation

import uk.gov.gds.ier.model.{InprogressOverseas, ApplicationType}
import uk.gov.gds.ier.step.ConfirmationStepController
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.IerApiService
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.validation.InProgressForm
import controllers.step.overseas.routes.ConfirmationController
import controllers.step.overseas.routes.PreviouslyRegisteredController
import controllers.routes.CompleteController
import com.google.inject.Inject
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.mustache.ErrorPageMustache

class ConfirmationStep @Inject() (val encryptionKeys: EncryptionKeys,
                                  val encryptionService: EncryptionService,
                                  val config: Config,
                                  val serialiser: JsonSerialiser,
                                  ierApi: IerApiService)
  extends ConfirmationStepController[InprogressOverseas]
  with ConfirmationForms
  with ErrorPageMustache
  with ConfirmationMustache {

  def factoryOfT() = InprogressOverseas()

  val routes = Routes(
    get = ConfirmationController.get,
    post = ConfirmationController.post,
    editGet = ConfirmationController.get,
    editPost = ConfirmationController.post
  )

  val validation = confirmationForm
  val previousRoute = Some(PreviouslyRegisteredController.get)

  def template(form:InProgressForm[InprogressOverseas]) = {
    Confirmation.confirmationPage(
      form.form,
      previousRoute.map(_.url).getOrElse("#"),
      routes.post.url
    )
  }

  def get = ValidSession requiredFor {
    request => application =>
      application.identifyApplication match {
        case ApplicationType.DontKnow => NotFound(ErrorPage.NotFound(request.path))
        case _ => Ok(template(InProgressForm(validation.fillAndValidate(application))))
      }
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

          ierApi.submitOverseasApplication(remoteClientIP, validApplication, Some(refNum))
          Redirect(CompleteController.complete()).flashing(
            "refNum" -> refNum,
            "postcode" -> "SW1A 1AA"
          )
        }
      )
  }
}
