package uk.gov.gds.ier.transaction.ordinary.confirmation

import controllers.step.ordinary.routes.{ConfirmationController, ContactController}
import controllers.routes.CompleteController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.service.{WithAddressService, AddressService}
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{ConfirmationStepController, Routes}
import uk.gov.gds.ier.service.apiservice.IerApiService
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.guice.WithRemoteAssets

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
    with WithAddressService
    with WithRemoteAssets {

  def factoryOfT() = InprogressOrdinary()

  val routes:Routes = Routes(
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

      //Ok(template(validation.fillAndValidate(appWithAddressLines)))
      Ok(mustache(validation.fillAndValidate(appWithAddressLines), routes.post, application).html)
  }

  def post = ValidSession requiredFor {
    implicit request => application =>
      validation.fillAndValidate(application).fold(
        hasErrors => {
          Ok(mustache(hasErrors, routes.post, application).html)
        },
        validApplication => {
          val refNum = ierApi.generateOrdinaryReferenceNumber(validApplication)
          val remoteClientIP = request.headers.get("X-Real-IP")

          val response = ierApi.submitOrdinaryApplication(
            remoteClientIP,
            validApplication,
            Some(refNum),
            request.getToken.map(_.timeTaken)
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

          Redirect(CompleteController.complete()).flashing(
            "refNum" -> refNum,
            "localAuthority" -> serialiser.toJson(response.localAuthority),
            "hasOtherAddress" -> validApplication.otherAddress.map(
              _.otherAddressOption.hasOtherAddress.toString).getOrElse(""),
            "backToStartUrl" -> config.ordinaryStartUrl,
            "showEmailConfirmation" -> (isPostalVoteEmailPresent | isContactEmailPresent).toString
          )
        }
      )
  }
}

