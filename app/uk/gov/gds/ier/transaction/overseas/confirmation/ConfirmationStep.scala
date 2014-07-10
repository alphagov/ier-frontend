package uk.gov.gds.ier.transaction.overseas.confirmation

import uk.gov.gds.ier.model.{WaysToVoteType, ApplicationType}
import uk.gov.gds.ier.step.ConfirmationStepController
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import controllers.step.overseas.routes.ConfirmationController
import controllers.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.mustache.ErrorPageMustache
import uk.gov.gds.ier.service.apiservice.IerApiService
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.guice.WithRemoteAssets
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.Routes

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
  def timeoutPage() = ErrorController.ordinaryTimeout

  val routes: Routes = Routes(
    get = ConfirmationController.get,
    post = ConfirmationController.post,
    editGet = ConfirmationController.get,
    editPost = ConfirmationController.post
  )

  val validation = confirmationForm

  def get = ValidSession requiredFor {
    implicit request => application =>
      application.identifyApplication match {
        case ApplicationType.DontKnow => NotFound(ErrorPage.NotFound(request.path))
        case _ => Ok(mustache(validation.fillAndValidate(application), routes.post, application).html)
      }
  }

  def post = ValidSession requiredFor {
    implicit request => application =>
      validation.fillAndValidate(application).fold(
        hasErrors => {
          Ok(mustache(hasErrors, routes.post, application).html)
        },
        validApplication => {
          val refNum = ierApi.generateOverseasReferenceNumber(validApplication)
          val remoteClientIP = request.headers.get("X-Real-IP")

          val response = ierApi.submitOverseasApplication(
            remoteClientIP,
            validApplication,
            Some(refNum),
            request.getToken.map(_.timeTaken)
          )

          logSession()

          val isPostalOrProxyVoteEmailPresent = validApplication.postalOrProxyVote.exists { postalVote =>
            (postalVote.typeVote != WaysToVoteType.InPerson) &
              postalVote.postalVoteOption.exists(_ == true) & postalVote.deliveryMethod.exists{ deliveryMethod =>
              deliveryMethod.deliveryMethod.exists(_ == "email") && deliveryMethod.emailAddress.exists(_.nonEmpty)
            }
          }

          val isContactEmailPresent = validApplication.contact.exists(
            _.email.exists{ emailContact =>
              emailContact.contactMe & emailContact.detail.exists(_.nonEmpty)
            }
          )

          Redirect(CompleteController.complete()).flashing(
            "refNum" -> refNum,
            "localAuthority" -> serialiser.toJson(response.localAuthority),
            "showEmailConfirmation" -> (isPostalOrProxyVoteEmailPresent | isContactEmailPresent).toString
          )
        }
      )
  }
}
