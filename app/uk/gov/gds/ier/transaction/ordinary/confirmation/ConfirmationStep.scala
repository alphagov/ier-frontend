package uk.gov.gds.ier.transaction.ordinary.confirmation

import controllers.step.ordinary.routes.{ConfirmationController, ContactController}
import controllers.routes.CompleteController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.service.{AddressService}
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{ConfirmationStepController, Routes}
import controllers.routes.RegisterToVoteController
import uk.gov.gds.ier.service.apiservice.IerApiService
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

class ConfirmationStep @Inject ()(
    val serialiser: JsonSerialiser,
    ierApi: IerApiService,
    addressService: AddressService,
    val config: Config,
    val encryptionService : EncryptionService)

  extends ConfirmationStepController[InprogressOrdinary]
    with ConfirmationForms
    with ConfirmationMustache{

  def factoryOfT() = InprogressOrdinary()

  val routes = Routes(
    get = ConfirmationController.get,
    post = ConfirmationController.post,
    editGet = ConfirmationController.get,
    editPost = ConfirmationController.post
  )

  val validation = confirmationForm
  val previousRoute = Some(ContactController.get)

  def template(form: ErrorTransformForm[InprogressOrdinary]): Html = {
    Confirmation.confirmationPage(
      form,
      previousRoute.map(_.url).getOrElse("#"),
      routes.post.url
    )
  }

  def get = ValidSession requiredFor {
    request => application =>
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

      Ok(template(validation.fillAndValidate(appWithAddressLines)))
  }

  def post = ValidSession requiredFor {
    request => application =>
      validation.fillAndValidate(application).fold(
        hasErrors => {
          Ok(template(hasErrors))
        },
        validApplication => {
          val refNum = ierApi.generateOrdinaryReferenceNumber(validApplication)
          val remoteClientIP = request.headers.get("X-Real-IP")

          ierApi.submitOrdinaryApplication(remoteClientIP, validApplication, Some(refNum))
          Redirect(CompleteController.complete()).flashing(
            "refNum" -> refNum,
            "postcode" -> validApplication.address.map(_.postcode).getOrElse(""),
            "hasOtherAddress" -> validApplication.otherAddress.map(
              _.otherAddressOption.hasOtherAddress.toString).getOrElse(""),
            "backToStartUrl" -> RegisterToVoteController.registerToVote.url.toString
          )
        }
      )
  }
}

