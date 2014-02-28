package uk.gov.gds.ier.transaction.ordinary.previousAddress

import controllers.step.ordinary.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.model.InprogressOrdinary
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.service.AddressService

import uk.gov.gds.ier.step.{Routes, OrdinaryStep}

class PreviousAddressFirstStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val encryptionKeys : EncryptionKeys,
    val addressService: AddressService)
  extends OrdinaryStep
  with PreviousAddressFirstMustache
  with PreviousAddressFirstForms {

  val validation = previousAddressFirstForm
  val previousRoute = Some(OtherAddressController.get)

  val routes = Routes(
    get = PreviousAddressFirstController.get,
    post = PreviousAddressFirstController.post,
    editGet = PreviousAddressFirstController.editGet,
    editPost = PreviousAddressFirstController.editPost
  )

  def nextStep(currentState: InprogressOrdinary) = {
    if (currentState.previousAddress.flatMap(_.movedRecently) == Some(true)) {
      controllers.step.ordinary.PreviousAddressPostcodeController.previousPostcodeAddressStep
    } else {
      controllers.step.ordinary.OpenRegisterController.openRegisterStep
    }
  }

  def template(form: InProgressForm[InprogressOrdinary], call:Call, backUrl: Option[Call]): Html = {
    previousAddressFirstStepMustache(
      form.form,
      call.url,
      backUrl.map(_.url)
    )
  }
}

