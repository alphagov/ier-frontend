package uk.gov.gds.ier.transaction.ordinary.confirmation

import controllers.step.ordinary.routes.ConfirmationController
import controllers.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.{WithAddressService, AddressService}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.ConfirmationStepController
import uk.gov.gds.ier.service.apiservice.IerApiService
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.guice.WithRemoteAssets
import uk.gov.gds.ier.transaction.complete.ConfirmationCookie
import uk.gov.gds.ier.session.ResultHandling
import uk.gov.gds.ier.langs.Language
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

class ConfirmationStep @Inject ()(
    val serialiser: JsonSerialiser,
    ierApi: IerApiService,
    val addressService: AddressService,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets
  ) extends ConfirmationStepController[InprogressOrdinary]
  with ConfirmationForms
  with ConfirmationMustache
  with ConfirmationCookieWriter
  with ResultHandling
  with WithAddressService
  with WithRemoteAssets {

  def factoryOfT() = InprogressOrdinary()
  def timeoutPage() = ErrorController.ordinaryTimeout

  val routing:Routes = Routes(
    get = ConfirmationController.get,
    post = ConfirmationController.post,
    editGet = ConfirmationController.get,
    editPost = ConfirmationController.post
  )

  val validation = confirmationForm

  def get = ValidSession requiredFor {
    implicit request => application =>
      val currentAddressLine = application.address.map { addressService.fillAddressLine(_) }

      val previousAddressLine = application.previousAddress.flatMap { prev =>
        if (prev.movedRecently.exists(_.hasPreviousAddress == true)) {
          prev.previousAddress.map { addressService.fillAddressLine(_) }
        } else {
          None
        }
      }

      val appWithAddressLines = application.copy(
        address = currentAddressLine,
        previousAddress = application.previousAddress.map{ prev =>
          prev.copy(previousAddress = previousAddressLine)
        }
      )

      Ok(mustache(validation.fillAndValidate(appWithAddressLines), routing.post, application).html)
  }

  def post = ValidSession requiredFor {
    implicit request => application =>
      validation.fillAndValidate(application).fold(
        hasErrors => {
          Ok(mustache(hasErrors, routing.post, application).html)
        },
        validApplication => {
          val refNum = ierApi.generateOrdinaryReferenceNumber(validApplication)
          val remoteClientIP = request.headers.get("X-Real-IP")

          val response = ierApi.submitOrdinaryApplication(
            ipAddress = remoteClientIP,
            applicant = validApplication,
            referenceNumber = Some(refNum),
            timeTaken = request.getToken.map(_.timeTaken),
            language = Language.getLang(request).language,
            sessionId = request.getToken.flatMap(_.refreshToken.id)
          )

          logSession()

          val isPostalVoteEmailPresent = validApplication.postalVote.exists { postalVote =>
            postalVote.postalVoteOption.exists(_ == true) & postalVote.deliveryMethod.exists{ deliveryMethod =>
              deliveryMethod.deliveryMethod.exists(_ == "email") && deliveryMethod.emailAddress.exists(_.nonEmpty)
            }
          }
          val isContactEmailPresent = validApplication.contact.exists(
            _.email.exists{ emailContact =>
              emailContact.contactMe & emailContact.detail.exists(_.nonEmpty)
            }
          )
          val hasOtherAddress = validApplication.otherAddress
            .map(_.otherAddressOption.hasOtherAddress).getOrElse(false)

          val completeStepData = ConfirmationCookie(
            refNum = refNum,
            authority = Some(response.localAuthority),
            hasOtherAddress = hasOtherAddress,
            backToStartUrl = config.ordinaryStartUrl,
            showEmailConfirmation = (isPostalVoteEmailPresent | isContactEmailPresent)
          )

          Redirect(CompleteController.complete())
            .emptySession()
            .addConfirmationCookieToSession(completeStepData)
        }
      )
  }
}

