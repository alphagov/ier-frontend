package uk.gov.gds.ier.transaction.ordinary.address

import controllers.step.ordinary.routes.{
  AddressController,
  AddressManualController,
  NinoController}
import controllers.step.ordinary.OtherAddressController
import com.google.inject.Inject
import play.api.mvc.Call
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.{
  InprogressOrdinary,
  PartialAddress,
  Addresses,
  PossibleAddress}
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{OrdinaryStep, Routes}
import uk.gov.gds.ier.validation.InProgressForm

class AddressManualStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val encryptionKeys: EncryptionKeys)
  extends OrdinaryStep
  with AddressMustache
  with AddressForms {

  val validation = manualAddressForm

  val previousRoute = Some(NinoController.get)

  val routes = Routes(
    get = AddressManualController.get,
    post = AddressManualController.post,
    editGet = AddressManualController.editGet,
    editPost = AddressManualController.editPost
  )

  def nextStep(currentState: InprogressOrdinary) = {
    OtherAddressController.otherAddressStep
  }

  def template(
      form: InProgressForm[InprogressOrdinary],
      call: Call,
      backUrl: Option[Call]) = {
    AddressMustache.manualPage(
      form,
      backUrl.map(_.url).getOrElse(""),
      call.url,
      AddressController.get.url
    )
  }
}
