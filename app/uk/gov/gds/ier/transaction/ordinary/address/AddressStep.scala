package uk.gov.gds.ier.transaction.ordinary.address

import controllers.step.ordinary.routes.{
  AddressController,
  AddressSelectController,
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

class AddressStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val encryptionKeys: EncryptionKeys,
    val addressService: AddressService)
  extends OrdinaryStep
  with AddressMustache
  with AddressForms {

  val validation = addressForm
  val previousRoute = Some(NinoController.get)

  val routes = Routes(
    get = AddressController.get,
    post = AddressController.lookup,
    editGet = AddressController.editGet,
    editPost = AddressController.lookup
  )

  def nextStep(currentState: InprogressOrdinary) = {
    OtherAddressController.otherAddressStep
  }

  def template(
      form: InProgressForm[InprogressOrdinary],
      call: Call,
      backUrl: Option[Call]) = {
    AddressMustache.lookupPage(
      form,
      backUrl.map(_.url).getOrElse(""),
      call.url
    )
  }

  def lookup = ValidSession requiredFor { implicit request => application =>
    lookupAddressForm.bindFromRequest().fold(
      hasErrors => {
        Ok(template(InProgressForm(hasErrors), routes.post, previousRoute))
      },
      success => {
        val mergedApplication = success.merge(application)
        Redirect(
          AddressSelectController.get
        ) storeInSession mergedApplication
      }
    )
  }
}
