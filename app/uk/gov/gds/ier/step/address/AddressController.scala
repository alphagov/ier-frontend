package uk.gov.gds.ier.step.address

import controllers.step._
import com.google.inject.Inject
import uk.gov.gds.ier.model.{PossibleAddress, Addresses}
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.controller.StepController
import play.api.data.Form
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressApplication
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.service.PlacesService

class AddressController @Inject ()(val serialiser: JsonSerialiser,
                                   val config: Config,
                                   val encryptionService : EncryptionService,
                                   val encryptionKeys : EncryptionKeys,
                                   val placesService: PlacesService)
  extends StepController
  with WithSerialiser
  with WithConfig
  with WithEncryption
  with AddressForms {

  val validation = addressForm
  val editPostRoute = routes.AddressController.editPost
  val stepPostRoute = routes.AddressController.post

  def template(form:InProgressForm, call:Call): Html = {
    val possibleAddresses = form(keys.possibleAddresses.jsonList).value match {
      case Some(possibleAddressJS) if !possibleAddressJS.isEmpty => {
        serialiser.fromJson[Addresses](possibleAddressJS)
      }
      case _ => Addresses(List.empty)
    }
    val possiblePostcode = form(keys.possibleAddresses.postcode).value

    val possible = possiblePostcode.map(PossibleAddress(possibleAddresses, _))
    views.html.steps.address(form, call, possible)
  }
  def goToNext(currentState: InprogressApplication): SimpleResult = {
    Redirect(routes.PreviousAddressController.get)
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

  def lookupAddress(success: InprogressApplication): InProgressForm = {
    val postcode = success.possibleAddresses.get.postcode
    val addressesList = placesService.lookupAddress(postcode)
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