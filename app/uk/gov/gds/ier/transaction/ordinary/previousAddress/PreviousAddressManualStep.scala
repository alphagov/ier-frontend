package uk.gov.gds.ier.transaction.ordinary.previousAddress

import controllers.step.ordinary.routes._
import com.google.inject.Inject
import play.api.mvc.Call
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.OrdinaryStep
import uk.gov.gds.ier.validation.InProgressForm
import play.api.mvc.Call
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.validation.InProgressForm
import scala.Some
import controllers.step.ordinary.OpenRegisterController

class PreviousAddressManualStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val encryptionKeys: EncryptionKeys)
  extends OrdinaryStep
  with PreviousAddressMustache
  with PreviousAddressForms {

  val validation = manualAddressForm

  val previousRoute = Some(PreviousAddressSelectController.get)

  val routes = Routes(
    get = PreviousAddressManualController.get,
    post = PreviousAddressManualController.post,
    editGet = PreviousAddressManualController.editGet,
    editPost = PreviousAddressManualController.editPost
  )

  def nextStep(currentState: InprogressOrdinary) = {
    OpenRegisterController.openRegisterStep
  }

  def template(
      form: InProgressForm[InprogressOrdinary],
      call: Call,
      backUrl: Option[Call]) = {
    PreviousAddressMustache.manualPage(
      form,
      backUrl.map(_.url).getOrElse(""),
      call.url,
      PreviousAddressPostcodeController.get.url
    )
  }
}
