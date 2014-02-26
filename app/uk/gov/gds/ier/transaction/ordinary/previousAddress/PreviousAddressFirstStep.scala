package uk.gov.gds.ier.transaction.ordinary.previousAddress

import controllers.step.ordinary.routes._
import controllers.step.ordinary.OpenRegisterController
import com.google.inject.Inject
import uk.gov.gds.ier.model.InprogressOrdinary
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.service.AddressService

import uk.gov.gds.ier.step.{Routes, OrdinaryStep}


class PreviousAddressFirstStep @Inject ()(val serialiser: JsonSerialiser,
                                           val config: Config,
                                           val encryptionService : EncryptionService,
                                           val encryptionKeys : EncryptionKeys,
                                           val addressService: AddressService)
  extends OrdinaryStep
  with PreviousAddressFirstMustache
  with PreviousAddressForms {

  val validation = previousAddressForm
  val previousRoute = Some(OtherAddressController.get)

  val routes = Routes(
    get = PreviousAddressFirstController.get,
    post = PreviousAddressFirstController.post,
    editGet = PreviousAddressFirstController.editGet,
    editPost = PreviousAddressFirstController.editPost
  )

  def nextStep(currentState: InprogressOrdinary) = {
    OpenRegisterController.openRegisterStep
  }

//  override def postMethod(postCall:Call, backUrl:Option[Call])(implicit manifest: Manifest[InprogressOrdinary]) = ValidSession requiredFor {
//    implicit request => application =>
//      logger.debug(s"POST request for ${request.path}")
//      validation.bindFromRequest().fold(
//        hasErrors => {
//          logger.debug(s"Form binding error: ${hasErrors.prettyPrint.mkString(", ")}")
//          Ok(template(InProgressForm(hasErrors), postCall, backUrl)) storeInSession application
//        },
//        success => {
//          logger.debug(s"Form binding successful")
//          if (success.previousAddress.get.findAddress) {
//            Ok(template(lookupAddress(success), postCall, backUrl)) storeInSession application
//          }
//          else {
//            val mergedApplication = success.merge(application)
//            goToNext(mergedApplication) storeInSession mergedApplication
//          }
//        }
//      )
//  }

  def template(form: InProgressForm[InprogressOrdinary], call:Call, backUrl: Option[Call]): Html = {
    previousAddressFirstStepMustache(
      form.form,
      call.url,
      backUrl.map(_.url)
    )
//    val possibleAddresses = form(keys.possibleAddresses.jsonList).value match {
//      case Some(possibleAddressJS) if !possibleAddressJS.isEmpty => {
//        serialiser.fromJson[Addresses](possibleAddressJS)
//      }
//      case _ => Addresses(List.empty)
//    }
//    val possiblePostcode = form(keys.possibleAddresses.postcode).value
//
//    val possible = possiblePostcode.map(PossibleAddress(possibleAddresses, _))
//    views.html.steps.previousAddress(form, call, possible, backUrl.map(_.url))
  }


//  def lookupAddress(success: InprogressOrdinary): InProgressForm[InprogressOrdinary] = {
//    val postcode = success.previousAddress.get.previousAddress.get.postcode
//    val addressesList = addressService.lookupPartialAddress(postcode)
//    val inProgressForm = InProgressForm(
//      validation.fill(
//        success.copy(
//          possibleAddresses = Some(PossibleAddress(Addresses(addressesList), postcode))
//        )
//      )
//    )
//    inProgressForm
//  }
}

