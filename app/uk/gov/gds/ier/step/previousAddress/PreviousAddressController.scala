package uk.gov.gds.ier.step.previousAddress

import controllers.step._
import com.google.inject.Inject
import uk.gov.gds.ier.model.{Addresses, PossibleAddress}
import uk.gov.gds.ier.step.address.AddressForms
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.controller.StepController
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressApplication
import play.api.templates.Html

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import play.mvc.Result
import uk.gov.gds.ier.service.AddressService

class PreviousAddressController @Inject ()(val serialiser: JsonSerialiser,
                                           val config: Config,
                                           val encryptionService : EncryptionService,
                                           val encryptionKeys : EncryptionKeys,
                                           val addressService: AddressService)
  extends StepController
  with WithSerialiser
  with WithConfig
  with WithEncryption
  with AddressForms
  with PreviousAddressForms {

  val validation = previousAddressForm
  val editPostRoute = routes.PreviousAddressController.editPost
  val stepPostRoute = routes.PreviousAddressController.post

  override def post = ValidSession storeAfter {
    implicit request => application =>
      logger.debug(s"POST request for ${request.path}")
      validation.bindFromRequest().fold(
        hasErrors => {
          logger.debug(s"Form binding error: ${hasErrors.prettyPrint.mkString(", ")}")
          (Ok(stepPage(InProgressForm(hasErrors))), application)
        },
        success => {
          logger.debug(s"Form binding successful")
          if (success.previousAddress.get.findAddress) {
            (Ok(stepPage(lookupAddress(success))),application)
          }
          else {
            val mergedApplication = merge(application, success)
            (goToNext(mergedApplication), mergedApplication)
          }
        }
      )
  }

  def template(form:InProgressForm, call:Call): Html = {
    val possibleAddresses = form(keys.possibleAddresses.jsonList).value match {
      case Some(possibleAddressJS) if !possibleAddressJS.isEmpty => {
        serialiser.fromJson[Addresses](possibleAddressJS)
      }
      case _ => Addresses(List.empty)
    }
    val possiblePostcode = form(keys.possibleAddresses.postcode).value

    val possible = possiblePostcode.map(PossibleAddress(possibleAddresses, _))
    views.html.steps.previousAddress(form, call, possible)
  }
  def goToNext(currentState: InprogressApplication): SimpleResult = {
    Redirect(routes.OtherAddressController.get)
  }

  def lookupAddress(success: InprogressApplication): InProgressForm = {
    val postcode = success.previousAddress.get.previousAddress.get.postcode
    val addressesList = addressService.lookupPartialAddress(postcode)
    val inProgressForm = InProgressForm(
      validation.fill(
        success.copy(
          possibleAddresses = Some(PossibleAddress(Addresses(addressesList), postcode))
        )
      )
    )
    inProgressForm
  }
}

