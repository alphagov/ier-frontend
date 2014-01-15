package uk.gov.gds.ier.transaction.ordinary.address

import controllers.step.ordinary.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.model.{InprogressOrdinary, PossibleAddress, Addresses, InprogressApplication}
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import play.api.data.Form
import play.api.mvc.{SimpleResult, Call}
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.OrdinaryStep

class AddressStep @Inject ()(val serialiser: JsonSerialiser,
                                   val config: Config,
                                   val encryptionService : EncryptionService,
                                   val encryptionKeys : EncryptionKeys,
                                   val addressService: AddressService)
  extends OrdinaryStep
  with AddressForms {

  val validation = addressForm
  val editPostRoute = AddressController.editPost
  val stepPostRoute = AddressController.post
  val previousRoute = Some(NinoController.get)

  def template(form:InProgressForm[InprogressOrdinary], call:Call, backUrl: Option[Call]): Html = {
    val possibleAddresses = form(keys.possibleAddresses.jsonList).value match {
      case Some(possibleAddressJS) if !possibleAddressJS.isEmpty => {
        serialiser.fromJson[Addresses](possibleAddressJS)
      }
      case _ => Addresses(List.empty)
    }
    val possiblePostcode = form(keys.possibleAddresses.postcode).value

    val possible = possiblePostcode.map(PossibleAddress(possibleAddresses, _))
    views.html.steps.address(form, call, possible, backUrl.map(_.url))
  }
  def goToNext(currentState: InprogressOrdinary): SimpleResult = {
    Redirect(PreviousAddressController.get)
  }

  def lookup = ValidSession requiredFor {
    implicit request => application =>
      addressLookupForm.bindFromRequest().fold(
        hasErrors => Ok(stepPage(InProgressForm(hasErrors))),
        success => Ok(stepPage(lookupAddress(success)))
      )
  }

  def editLookup = ValidSession requiredFor {
    implicit request => application =>
      addressLookupForm.bindFromRequest().fold(
        hasErrors => Ok(editPage(InProgressForm(hasErrors))),
        success => Ok(editPage(lookupAddress(success)))
      )
  }

  def lookupAddress(success: InprogressOrdinary): InProgressForm[InprogressOrdinary] = {
    val postcode = success.possibleAddresses.get.postcode
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
