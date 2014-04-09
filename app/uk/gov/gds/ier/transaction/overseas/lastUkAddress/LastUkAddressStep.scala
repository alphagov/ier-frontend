package uk.gov.gds.ier.transaction.overseas.lastUkAddress

import controllers.step.overseas.routes.{
  LastUkAddressController,
  LastUkAddressSelectController,
  DateLeftUkController}
import controllers.step.overseas.{
  NameController,
  PassportCheckController}
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.ApplicationType
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{OverseaStepWithNewMustache, Routes}
import uk.gov.gds.ier.form.OverseasFormImplicits
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.transaction.crown.address.WithAddressService

class LastUkAddressStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService)
  extends OverseaStepWithNewMustache
  with LastUkAddressLookupMustache
  with LastUkAddressForms
  with OverseasFormImplicits
  with WithAddressService {

  val validation = lastUkAddressForm

  val previousRoute = Some(DateLeftUkController.get)

  val routes = Routes(
    get = LastUkAddressController.get,
    post = LastUkAddressController.lookup,
    editGet = LastUkAddressController.editGet,
    editPost = LastUkAddressController.lookup
  )

  def nextStep(currentState: InprogressOverseas) = {
    currentState.identifyApplication match {
      case ApplicationType.RenewerVoter => NameController.nameStep
      case ApplicationType.DontKnow => this
      case _ => PassportCheckController.passportCheckStep
    }
  }

  def lookup = ValidSession requiredFor { implicit request => application =>
    lookupAddressForm.bindFromRequest().fold(
      hasErrors => {
        Ok(template(hasErrors, routes.post, previousRoute))
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
