package uk.gov.gds.ier.transaction.crown.previousAddress

import controllers.step.crown.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.CrownStep
import controllers.step.crown.NationalityController
import uk.gov.gds.ier.model.Addresses
import play.api.mvc.Call
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.model.PossibleAddress
import uk.gov.gds.ier.model.InprogressCrown
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.PartialPreviousAddress
import uk.gov.gds.ier.model.MovedHouseOption

class PreviousAddressSelectStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService)
  extends CrownStep
  with PreviousAddressMustache
  with PreviousAddressForms {

  val validation = selectAddressFormForPreviousAddress

  val previousRoute = Some(PreviousAddressPostcodeController.get)

  val routes = Routes(
    get = PreviousAddressSelectController.get,
    post = PreviousAddressSelectController.post,
    editGet = PreviousAddressSelectController.editGet,
    editPost = PreviousAddressSelectController.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    NationalityController.nationalityStep
  }

  override val onSuccess = TransformApplication { currentState =>
    val address = currentState.previousAddress.flatMap(_.previousAddress)
    val addressWithAddressLine = address.map {
      addressService.fillAddressLine(_)
    }

    currentState.copy(
      previousAddress = Some(PartialPreviousAddress(
        movedRecently = Some(MovedHouseOption.Yes),
        addressWithAddressLine
      )),
      possibleAddresses = None
    )
  } andThen GoToNextIncompleteStep()

  def template(
      form: ErrorTransformForm[InprogressCrown],
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
      val postcode = form(keys.previousAddress.postcode).value
      lookupAddresses(postcode)
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

  private def lookupAddresses(
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
