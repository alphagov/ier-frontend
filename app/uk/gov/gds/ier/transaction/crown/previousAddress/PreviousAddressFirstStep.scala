package uk.gov.gds.ier.transaction.crown.previousAddress

import controllers.step.crown.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.model.InprogressCrown
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.service.AddressService

import uk.gov.gds.ier.step.{Routes, CrownStep}

class PreviousAddressFirstStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val addressService: AddressService)
  extends CrownStep
  with PreviousAddressFirstMustache
  with PreviousAddressFirstForms {

  val validation = previousAddressFirstForm
  val previousRoute = Some(AddressController.get)

  val routes = Routes(
    get = PreviousAddressFirstController.get,
    post = PreviousAddressFirstController.post,
    editGet = PreviousAddressFirstController.editGet,
    editPost = PreviousAddressFirstController.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    if (currentState.previousAddress.flatMap(_.movedRecently) == Some(true)) {
      controllers.step.crown.PreviousAddressPostcodeController.previousPostcodeAddressStep
    } else {
      controllers.step.crown.NationalityController.nationalityStep
    }
  }

  def template(form: InProgressForm[InprogressCrown], call:Call, backUrl: Option[Call]): Html = {
    previousAddressFirstStepMustache(
      form.form,
      call.url,
      backUrl.map(_.url)
    )
  }
}

