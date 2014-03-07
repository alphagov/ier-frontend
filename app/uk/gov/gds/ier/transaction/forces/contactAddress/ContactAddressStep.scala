package uk.gov.gds.ier.transaction.forces.contactAddress

import controllers.step.forces.OpenRegisterController
import controllers.step.forces.routes.{ContactAddressController, RankController}
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import play.api.mvc.Call
import uk.gov.gds.ier.model.{ContactAddress, InprogressForces}
import play.api.templates.Html
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.{ForcesStep, Routes}

class ContactAddressStep @Inject ()(val serialiser: JsonSerialiser,
                                       val config: Config,
                                       val encryptionService : EncryptionService,
                                       val encryptionKeys : EncryptionKeys)
  extends ForcesStep
  with ContactAddressForms
  with ContactAddressMustache{

  val validation = contactAddressForm
  val previousRoute = Some(RankController.get)

  val routes = Routes(
    get = ContactAddressController.get,
    post = ContactAddressController.post,
    editGet = ContactAddressController.editGet,
    editPost = ContactAddressController.editPost
  )

//  override def get(implicit manifest: Manifest[InprogressForces]) = ValidSession requiredFor {
//    request => application =>
//      logger.debug(s"GET request for ${request.path}")
//
//      val ukAddress = application.address
//      val newCurrentState =
//        if (ukAddress.isDefined) {
//          val addressLine = ukAddress.get.addressLine
//          if (addressLine.isDefined)  {
//            application.copy(contactAddress =
//              Some(updateContactAddressWithAdressLines(addressLine, application.contactAddress.get))
//            )
//          }
//          else application
//        }
//        else application
//
//
//      Ok(template(InProgressForm(validation.fill(application)), routes.post, previousRoute))
//  }

  def template(
      form:InProgressForm[InprogressForces],
      postEndpoint:Call,
      backEndpoint: Option[Call]): Html = {
    contactAddressMustache(form.form, postEndpoint, backEndpoint)
  }

//  override def postSuccess(currentState: InprogressForces):InprogressForces = {
//
//    val ukAddress = currentState.address
//    val newCurrentState =
//      if (ukAddress.isDefined) {
//        val addressLine = ukAddress.get.addressLine
//        if (addressLine.isDefined)  {
//          currentState.copy(contactAddress =
//            Some(updateContactAddressWithAdressLines(addressLine, currentState.contactAddress.get))
//          )
//        }
//        else currentState
//      }
//      else currentState
//
//    newCurrentState.contactAddress.get.postcode
//
//
//    newCurrentState
//  }


//  private def updateContactAddressWithAdressLines(addressLine: Option[String], currentContactAddress: ContactAddress): ContactAddress = {
//    val adaptedAddressList: List[String] = splitAddressLineIntoList(addressLine)
//    currentContactAddress.copy(
//        addressLine1 = if (adaptedAddressList.size > 0) Some(adaptedAddressList(0)) else None,
//        addressLine2 = if (adaptedAddressList.size > 1) Some(adaptedAddressList(1)) else None,
//        addressLine3 = if (adaptedAddressList.size > 2) Some(adaptedAddressList(2)) else None,
//        addressLine4 = if (adaptedAddressList.size > 3) Some(adaptedAddressList(3)) else None,
//        addressLine5 = if (adaptedAddressList.size > 4) Some(adaptedAddressList(4)) else None
//    )
//  }
//
//  private def splitAddressLineIntoList(addressLine: Option[String]): List[String] = {
//    val addressList = addressLine.get.split(",").toList
//    val adaptedAddressList =
//      if (addressList.size > 5) {
//        val firstAddressLineList = (for (i <- 0 until addressList.size - 4) yield addressList(i))
//        val firstAddressLine = firstAddressLineList.mkString(",")
//        val restOfList: List[String] = (for (i <- addressList.size - 4 until addressList.size) yield addressList(i)).toList
//        firstAddressLine :: restOfList
//      }
//      else addressList
//    adaptedAddressList
//  }

  def nextStep(currentState: InprogressForces) = {
    OpenRegisterController.openRegisterStep
  }
}

