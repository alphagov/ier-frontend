package uk.gov.gds.ier.transaction.ordinary.address

import controllers.step.ordinary.routes.{AddressController, NinoController}
import controllers.step.ordinary.PreviousAddressController
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
import uk.gov.gds.ier.step.{OrdinaryStep, Routes}
import uk.gov.gds.ier.transaction.address.AddressMustache
import uk.gov.gds.ier.logging.Logging

class AddressStep @Inject ()(val serialiser: JsonSerialiser,
                             val config: Config,
                             val encryptionService : EncryptionService,
                             val encryptionKeys : EncryptionKeys,
                             val addressService: AddressService)
  extends OrdinaryStep
  with AddressForms 
  with AddressMustache 
  with Logging {

  val validation = addressForm
  val previousRoute = Some(NinoController.get)

  val routes = Routes(
    get = AddressController.get,
    post = AddressController.post,
    editGet = AddressController.editGet,
    editPost = AddressController.editPost
  )

  def nextStep(currentState: InprogressOrdinary) = {
    PreviousAddressController.previousAddressStep
  }

  def template(form:InProgressForm[InprogressOrdinary], call:Call, backUrl: Option[Call]): Html = {
    transformFormStepToMustacheData(form.form, call, backUrl)
  }
  
  def lookup = ValidSession requiredFor {
    implicit request => application =>
      addressLookupForm.bindFromRequest().fold(
        hasErrors => {
            Ok(template(InProgressForm(hasErrors), routes.post, previousRoute))
        },
        success => {
            Ok(template(lookupAddress(success), routes.post, previousRoute))
        }
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
