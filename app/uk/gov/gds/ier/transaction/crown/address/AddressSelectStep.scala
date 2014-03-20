package uk.gov.gds.ier.transaction.crown.address

import controllers.step.crown.routes._
import controllers.step.crown.NationalityController
import com.google.inject.Inject
import play.api.mvc.Call
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.{LastUkAddress, InprogressCrown, Addresses, PossibleAddress}
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{CrownStep, Routes}
import uk.gov.gds.ier.validation.InProgressForm

class AddressSelectStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService)
  extends CrownStep
  with AddressMustache
  with AddressForms {

  val validation = addressForm

  val previousRoute = Some(StatementController.get)

  val routes = Routes(
    get = AddressSelectController.get,
    post = AddressSelectController.post,
    editGet = AddressSelectController.editGet,
    editPost = AddressSelectController.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    NationalityController.nationalityStep
  }

  override def postSuccess(currentState: InprogressCrown) = {
    val addressWithAddressLine = currentState.address.map {
      address => addressService.fillAddressLine(address.address.get)
    }

    currentState.copy(
      address = Some(LastUkAddress(
        hasUkAddress = currentState.address.get.hasUkAddress,
        address = addressWithAddressLine
      )),
      possibleAddresses = None
    )
  }

  def template(
      form: InProgressForm[InprogressCrown],
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
      lookupAddresses(form(keys.address.postcode).value)
    }

    AddressMustache.selectPage(
      form,
      backUrl.map(_.url).getOrElse(""),
      call.url,
      AddressController.get.url,
      AddressManualController.get.url,
      maybeAddresses
    )
  }

  private[address] def lookupAddresses(
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
