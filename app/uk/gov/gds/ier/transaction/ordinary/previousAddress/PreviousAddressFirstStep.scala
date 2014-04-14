package uk.gov.gds.ier.transaction.ordinary.previousAddress

import controllers.step.ordinary.routes._
import controllers.step.ordinary.PreviousAddressPostcodeController._
import controllers.step.ordinary.OpenRegisterController._
import com.google.inject.Inject
import uk.gov.gds.ier.model.{MovedHouseOption}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.service.AddressService

import uk.gov.gds.ier.step.{Routes, OrdinaryStep}
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

class PreviousAddressFirstStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
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
    currentState.previousAddress.flatMap(_.movedRecently) match {
      case Some(MovedHouseOption.MovedFromAbroad) => previousPostcodeAddressStep
      case Some(MovedHouseOption.MovedFromUk) => previousPostcodeAddressStep
      case Some(MovedHouseOption.NotMoved) => openRegisterStep
      case _ => this
    }
  }
}

