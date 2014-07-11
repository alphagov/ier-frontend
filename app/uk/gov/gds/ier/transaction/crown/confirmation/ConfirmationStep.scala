package uk.gov.gds.ier.transaction.crown.confirmation

import uk.gov.gds.ier.step.ConfirmationStepController
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import controllers.step.crown.routes.ConfirmationController
import controllers.routes.{CompleteController, ErrorController}
import com.google.inject.Inject
import uk.gov.gds.ier.service.apiservice.IerApiService
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.guice.WithRemoteAssets
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.model.{WaysToVoteType, ApplicationType}
import uk.gov.gds.ier.transaction.complete.ConfirmationCookie
import uk.gov.gds.ier.transaction.ordinary.confirmation.ConfirmationCookieWriter
import uk.gov.gds.ier.session.ResultHandling

class ConfirmationStep @Inject() (
    val encryptionService: EncryptionService,
    val config: Config,
    val serialiser: JsonSerialiser,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    ierApi: IerApiService)
  extends ConfirmationStepController[InprogressCrown]
  with ConfirmationForms
  with ConfirmationMustache
  with ConfirmationCookieWriter
  with ResultHandling
  with WithRemoteAssets {

  def factoryOfT() = InprogressCrown()
  def timeoutPage() = ErrorController.crownTimeout

  val routing: Routes = Routes(
    get = ConfirmationController.get,
    post = ConfirmationController.post,
    editGet = ConfirmationController.get,
    editPost = ConfirmationController.post
  )

  val validation = confirmationForm

  def get = ValidSession requiredFor {
    implicit request => application =>
      Ok(mustache(validation.fillAndValidate(application), routing.post, application).html)
  }

  def post = ValidSession requiredFor {
    implicit request => application =>
      validation.fillAndValidate(application).fold(
        hasErrors => {
          Ok(mustache(hasErrors, routing.post, application).html)
        },
        validApplication => {
          val refNum = ierApi.generateCrownReferenceNumber(validApplication)
          val remoteClientIP = request.headers.get("X-Real-IP")

          val response = ierApi.submitCrownApplication(
            remoteClientIP,
            validApplication,
            Some(refNum),
            request.getToken.map(_.timeTaken),
            sessionId = request.getToken.map(_.id)
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

          val completeStepData = ConfirmationCookie(
            refNum = refNum,
            authority = Some(response.localAuthority),
            backToStartUrl = config.ordinaryStartUrl,
            showEmailConfirmation = (isPostalOrProxyVoteEmailPresent | isContactEmailPresent)
          )

          Redirect(CompleteController.complete())
            .emptySession()
            .addConfirmationCookieToSession(completeStepData)
        }
      )
  }
}
