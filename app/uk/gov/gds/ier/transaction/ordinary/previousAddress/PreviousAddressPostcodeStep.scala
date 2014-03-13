package uk.gov.gds.ier.transaction.ordinary.previousAddress

import controllers.step.ordinary.routes._
import com.google.inject.Inject
import play.api.mvc.Call
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.InprogressOrdinary
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{OrdinaryStep, Routes}
import uk.gov.gds.ier.validation.InProgressForm

class PreviousAddressPostcodeStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService)
  extends OrdinaryStep
  with PreviousAddressMustache
  with PreviousAddressForms {

  val validation = postcodeAddressFormForPreviousAddress

  val previousRoute = Some(PreviousAddressFirstController.get)

  val routes = Routes(
    get = PreviousAddressPostcodeController.get,
    post = PreviousAddressPostcodeController.lookup,
    editGet = PreviousAddressPostcodeController.editGet,
    editPost = PreviousAddressPostcodeController.lookup
  )

  def nextStep(currentState: InprogressOrdinary) = {
    //OpenRegisterController.openRegisterStep
    controllers.step.ordinary.PreviousAddressSelectController.previousAddressSelectStep
  }

  def template(
      form: InProgressForm[InprogressOrdinary],
      call: Call,
      backUrl: Option[Call]) = {
    PreviousAddressMustache.postcodePage(
      form,
      backUrl.map(_.url).getOrElse(""),
      call.url
    )
  }

  def lookup = ValidSession requiredFor { implicit request => application =>
    validation.bindFromRequest().fold(
      hasErrors => {
        Ok(template(InProgressForm(hasErrors), routes.post, previousRoute))
      },
      success => {
        val mergedApplication = success.merge(application)
        Redirect(
          PreviousAddressSelectController.get
        ) storeInSession mergedApplication
      }
    )
  }
}
