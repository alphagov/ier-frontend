package uk.gov.gds.ier.transaction.crown.confirmation

import uk.gov.gds.ier.model.InprogressCrown
import uk.gov.gds.ier.step.ConfirmationStepController
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.IerApiService
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.validation.InProgressForm
import controllers.step.crown.routes.ConfirmationController
import controllers.step.crown.routes.StatementController
import controllers.routes.CompleteController
import com.google.inject.Inject
import uk.gov.gds.ier.step.Routes
import play.api.templates.Html
import play.api.mvc.Call


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

  override def templateWithApplication(form:InProgressForm[InprogressCrown]) = {
    application:InprogressCrown =>
      Confirmation.confirmationPage(
        application,
        form,
        previousRoute.map(_.url).getOrElse("#"),
        routes.post.url
      )
  }

  def template(form:InProgressForm[InprogressCrown]) = Html.empty

  def get = ValidSession requiredFor {
    request => application =>
      Ok(templateWithApplication(InProgressForm(validation.fillAndValidate(application)))(application))
  }

  def post = ValidSession requiredFor {
    request => application =>
      validation.fillAndValidate(application).fold(
        hasErrors => {
          Ok(templateWithApplication(InProgressForm(hasErrors))(application))
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
