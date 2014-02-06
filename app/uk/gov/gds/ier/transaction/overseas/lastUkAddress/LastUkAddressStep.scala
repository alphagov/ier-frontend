package uk.gov.gds.ier.transaction.overseas.lastUkAddress

import controllers.step.overseas.routes.{
  LastUkAddressController,
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
  PossibleAddress
}
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.validation.InProgressForm

class LastUkAddressStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val encryptionKeys: EncryptionKeys,
    val addressService: AddressService)
  extends OverseaStep
  with LastUkAddressMustache
  with LastUkAddressForms {

  val validation = lastUkAddressForm

  val previousRoute = Some(DateLeftUkController.get)

  val routes = Routes(
    get = LastUkAddressController.get,
    post = LastUkAddressController.lookup,
    editGet = LastUkAddressController.editGet,
    editPost = LastUkAddressController.lookup
  )

  def nextStep(currentState: InprogressOverseas) = {
    NameController.nameStep
  }

  def template(
      form: InProgressForm[InprogressOverseas],
      call: Call,
      backUrl: Option[Call]) = {
    LastUkAddressMustache.lookupPage(
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
          LastUkAddressSelectController.get
        ) storeInSession mergedApplication
      }
    )
  }
}
