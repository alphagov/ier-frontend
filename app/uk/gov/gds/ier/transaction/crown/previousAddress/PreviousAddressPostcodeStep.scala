package uk.gov.gds.ier.transaction.crown.previousAddress

import controllers.step.crown.routes._
import com.google.inject.Inject
import play.api.mvc.Call
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.InprogressCrown
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{CrownStep, Routes}
import uk.gov.gds.ier.validation.ErrorTransformForm

class PreviousAddressPostcodeStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService)
  extends CrownStep
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

  def nextStep(currentState: InprogressCrown) = {
    controllers.step.crown.PreviousAddressSelectController.previousAddressSelectStep
  }

  def template(
      form: ErrorTransformForm[InprogressCrown],
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
        Ok(template(hasErrors, routes.post, previousRoute))
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
