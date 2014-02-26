package uk.gov.gds.ier.transaction.ordinary.previousAddress

import controllers.step.ordinary.routes._
import com.google.inject.Inject
import play.api.mvc.Call
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{OrdinaryStep, Routes}
import uk.gov.gds.ier.validation.InProgressForm
import controllers.step.ordinary.OpenRegisterController
import uk.gov.gds.ier.model.Addresses
import play.api.mvc.Call
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.model.PossibleAddress
import uk.gov.gds.ier.model.InprogressOrdinary
import uk.gov.gds.ier.validation.InProgressForm
import scala.Some
import uk.gov.gds.ier.model.PartialPreviousAddress

class PreviousAddressSelectStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val encryptionKeys: EncryptionKeys,
    val addressService: AddressService)
  extends OrdinaryStep
  with PreviousAddressMustache
  with PreviousAddressForms {

  val validation = previousAddressForm

  val previousRoute = Some(PreviousAddressPostcodeController.get)

  val routes = Routes(
    get = PreviousAddressSelectController.get,
    post = PreviousAddressSelectController.post,
    editGet = PreviousAddressSelectController.editGet,
    editPost = PreviousAddressSelectController.editPost
  )

  def nextStep(currentState: InprogressOrdinary) = {
    OpenRegisterController.openRegisterStep
  }

  override def postSuccess(currentState: InprogressOrdinary) = {
    val address = currentState.previousAddress.map(_.previousAddress.get)
    val addressWithAddressLine = address.map {
      addressService.fillAddressLine(_)
    }

    currentState.copy(
      previousAddress = Some(PartialPreviousAddress(
        movedRecently = Some(true),
        findAddress = false, // FIXME: remove, unused
        addressWithAddressLine
      )),
      possibleAddresses = None
    )
  }

  def template(
      form: InProgressForm[InprogressOrdinary],
      call: Call,
      backUrl: Option[Call]) = {

    val storedAddresses = for(
      jsonList <- form(keys.possibleAddresses.jsonList).value;
      postcode <- form(keys.possibleAddresses.postcode).value
    ) yield {
      PossibleAddress(
        jsonList = serialiser.fromJson[Addresses](jsonList),
        postcode = postcode
      )
    }

    val maybeAddresses = storedAddresses.orElse {
      lookupAddresses(form(keys.lastUkAddress.postcode).value)
    }

    PreviousAddressMustache.selectPage(
      form,
      backUrl.map(_.url).getOrElse(""),
      call.url,
      PreviousAddressPostcodeController.get.url,
      PreviousAddressManualController.get.url,
      maybeAddresses
    )
  }

  private def lookupAddresses(  // FIXME:should be (?) private[uk.gov.gds.ier.transaction.ordinary.previousAddress]
      maybePostcode:Option[String]): Option[PossibleAddress] = {

    maybePostcode.map { postcode =>
      val addresses = addressService.lookupPartialAddress(postcode)
      PossibleAddress(
        jsonList = Addresses(addresses),
        postcode = postcode
      )
    }
  }
}
