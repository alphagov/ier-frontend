package uk.gov.gds.ier.transaction.overseas.lastUkAddress

import controllers.step.overseas.routes.{
  LastUkAddressController,
  LastUkAddressManualController,
  LastUkAddressSelectController,
  DateLeftUkController}
import controllers.step.overseas.NameController
import com.google.inject.Inject
import play.api.mvc.Call
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.{
  InprogressOverseas,
  PartialAddress,
  Addresses,
  PossibleAddress}
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.validation.InProgressForm

class LastUkAddressSelectStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val encryptionKeys: EncryptionKeys,
    val addressService: AddressService)
  extends OverseaStep
  with LastUkAddressMustache
  with LastUkAddressForms {

  val validation = selectAddressForm

  val previousRoute = Some(DateLeftUkController.get)

  val routes = Routes(
    get = LastUkAddressSelectController.get,
    post = LastUkAddressSelectController.post,
    editGet = LastUkAddressSelectController.editGet,
    editPost = LastUkAddressSelectController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    NameController.nameStep
  }

  override def postSuccess(currentState: InprogressOverseas) = {
    val addressWithAddressLine = currentState.lastUkAddress.map {
      addressService.fillAddressLine(_)
    }

    currentState.copy(
      lastUkAddress = addressWithAddressLine,
      possibleAddresses = None
    )
  }

  def template(
      form: InProgressForm[InprogressOverseas],
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

    LastUkAddressMustache.selectPage(
      form,
      backUrl.map(_.url).getOrElse(""),
      call.url,
      LastUkAddressController.get.url,
      LastUkAddressManualController.get.url,
      maybeAddresses
    )
  }

  private[lastUkAddress] def lookupAddresses(
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
