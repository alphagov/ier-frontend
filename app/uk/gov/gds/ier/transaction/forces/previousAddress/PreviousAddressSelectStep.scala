package uk.gov.gds.ier.transaction.forces.previousAddress

import controllers.step.forces.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.ForcesStep
import controllers.step.forces.NationalityController
import uk.gov.gds.ier.model.Addresses
import play.api.mvc.Call
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.model.PossibleAddress
import uk.gov.gds.ier.model.InprogressForces
import uk.gov.gds.ier.validation.InProgressForm
import scala.Some
import uk.gov.gds.ier.model.PartialPreviousAddress

class PreviousAddressSelectStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionKeys : EncryptionKeys,
    val encryptionService: EncryptionService,
    val addressService: AddressService)
  extends ForcesStep
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

  def nextStep(currentState: InprogressForces) = {
    NationalityController.nationalityStep
  }

  override def postSuccess(currentState: InprogressForces) = {
    val address = currentState.previousAddress.flatMap(_.previousAddress)
    val addressWithAddressLine = address.map {
      addressService.fillAddressLine(_)
    }

    currentState.copy(
      previousAddress = Some(PartialPreviousAddress(
        movedRecently = Some(true),
        addressWithAddressLine
      )),
      possibleAddresses = None
    )
  }

  def template(
      form: InProgressForm[InprogressForces],
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
