package uk.gov.gds.ier.transaction.overseas.parentsAddress

import controllers.step.overseas.routes._
import controllers.step.overseas.PassportCheckController
import com.google.inject.Inject
import play.api.mvc.Call
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.{
  InprogressOverseas,
  Addresses,
  PossibleAddress}
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.validation.InProgressForm

class ParentsAddressSelectStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService)
  extends OverseaStep
  with ParentsAddressMustache
  with ParentsAddressForms {

  val validation = parentsAddressForm

  val previousRoute = Some(DateLeftUkController.get)

  val routes = Routes(
    get = ParentsAddressSelectController.get,
    post = ParentsAddressSelectController.post,
    editGet = ParentsAddressSelectController.editGet,
    editPost = ParentsAddressSelectController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    PassportCheckController.passportCheckStep
  }

  override val onSuccess = TransformApplication { currentState =>
    val addressWithAddressLine = currentState.parentsAddress.map {
      addressService.fillAddressLine(_)
    }

    currentState.copy(
      parentsAddress = addressWithAddressLine,
      possibleAddresses = None
    )
  } andThen GoToNextIncompleteStep()

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
      lookupAddresses(form(keys.parentsAddress.postcode).value)
    }

    ParentsAddressMustache.selectPage(
      form,
      backUrl.map(_.url).getOrElse(""),
      call.url,
      ParentsAddressController.get.url,
      ParentsAddressManualController.get.url,
      maybeAddresses
    )
  }

  private[parentsAddress] def lookupAddresses(
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
