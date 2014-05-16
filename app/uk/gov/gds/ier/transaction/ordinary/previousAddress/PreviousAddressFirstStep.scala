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
import uk.gov.gds.ier.assets.RemoteAssets

class PreviousAddressFirstStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets)
  extends OrdinaryStep
  with PreviousAddressFirstMustache
  with PreviousAddressFirstForms {

  val validation = previousAddressFirstForm

  val routes = Routes(
    get = PreviousAddressFirstController.get,
    post = PreviousAddressFirstController.post,
    editGet = PreviousAddressFirstController.editGet,
    editPost = PreviousAddressFirstController.editPost
  )

  def nextStep(currentState: InprogressOrdinary) = {
    val nextAddressStep = currentState.previousAddress.map(_.previousAddress) match {
      case Some(address) =>
        if (address.exists(_.postcode.isEmpty)) { previousPostcodeAddressStep }
        else if (address.exists(_.manualAddress.isDefined)) { controllers.step.ordinary.PreviousAddressManualController.previousAddressManualStep }
        else if (address.exists(_.uprn.isDefined)) { controllers.step.ordinary.PreviousAddressSelectController.previousAddressSelectStep }
        else previousPostcodeAddressStep
      case _ => previousPostcodeAddressStep
    }

    currentState.previousAddress.flatMap(_.movedRecently) match {
      case Some(MovedHouseOption.MovedFromAbroadRegistered) => nextAddressStep
      case Some(MovedHouseOption.MovedFromAbroadNotRegistered) => openRegisterStep
      case Some(MovedHouseOption.MovedFromUk) => nextAddressStep
      case Some(MovedHouseOption.NotMoved) => openRegisterStep
      case _ => this
    }
  }

  override val onSuccess = TransformApplication { currentApplication =>

    val clearAddress = currentApplication.previousAddress.flatMap(
      _.movedRecently match {
        case Some(MovedHouseOption.NotMoved) => Some(true)
        case Some(MovedHouseOption.MovedFromAbroadNotRegistered) => Some(true)
        case _ => Some(false)
      }
    ).getOrElse(false)

    if(clearAddress){
      val clearedAddress = currentApplication.previousAddress.map{ _.copy(previousAddress = None) }

      currentApplication.copy(
        previousAddress = clearedAddress,
        possibleAddresses = None)
    } else {
      currentApplication
    }

  } andThen BranchOn (_.previousAddress.map(_.movedRecently)) {
    case Some(Some(MovedHouseOption.MovedFromAbroadRegistered)) => GoToNextStep()
    case Some(Some(MovedHouseOption.MovedFromUk)) => GoToNextStep()
    case _ => GoToNextIncompleteStep()
  }

}

