package uk.gov.gds.ier.transaction.forces.contactAddress

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.model.{PartialAddress, InprogressForces}

trait ContactAddressMustache extends StepMustache {

  case class ContactAddressModel(
      question:Question,
      contactAddressFieldSet: FieldSet,
      ukAddress: UKContactAddressModel,
      bfpoAddress: BFPOContactAddressModel,
      otherAddress: OtherContactAddressModel
  )

  case class OtherContactAddressModel(
      otherAddressOption: Field,
      otherAddressLine1: Field,
      otherAddressLine2: Field,
      otherAddressLine3: Field,
      otherAddressLine4: Field,
      otherAddressLine5: Field,
      otherAddressPostcode: Field,
      otherAddressCountry: Field
  )

  case class BFPOContactAddressModel(
      BFPOAddressOption: Field,
      BFPOAddressLine1: Field,
      BFPOAddressLine2: Field,
      BFPOAddressLine3: Field,
      BFPOAddressLine4: Field,
      BFPOAddressLine5: Field,
      BFPOAddressPostcode: Field
  )

  case class UKContactAddressModel(
      ukAddressOption: Field,
      ukAddressLineText: Field
//      ukAddressLine1: Field,
//      ukAddressLine2: Field,
//      ukAddressLine3: Field,
//      ukAddressLine4: Field,
//      ukAddressLine5: Field,
//      ukAddressPostcode: Field
  )
  
  def transformFormStepToMustacheData(
      form:ErrorTransformForm[InprogressForces],
      post: Call,
      back: Option[Call]): ContactAddressModel = {

    implicit val progressForm = form

    val application = progressForm.value

    val ukAddress = application.map(_.address).getOrElse(None)

    val ukAddressToBeShown = extractUkAddressText(ukAddress, form)


//    val adaptedAddressList: List[String] =
//        if (ukAddress.isDefined) {
//          val addressLine = ukAddress.get.addressLine
//          if (addressLine.isDefined)  {
//            splitAddressLineIntoList(addressLine)
//          }
//          else List.empty
//        }
//        else List.empty


//    val ukAddressLine1 = if (adaptedAddressList.size > 0) Some(adaptedAddressList(0))
//                         else form(keys.contactAddress.ukContactAddress.addressLine1.key).value
//
//    val ukAddressLine2 = if (adaptedAddressList.size > 1) Some(adaptedAddressList(1))
//                         else form(keys.contactAddress.ukContactAddress.addressLine2.key).value
//
//    val ukAddressLine3 = if (adaptedAddressList.size > 2) Some(adaptedAddressList(2))
//                         else form(keys.contactAddress.ukContactAddress.addressLine3.key).value
//
//    val ukAddressLine4 = if (adaptedAddressList.size > 3) Some(adaptedAddressList(3))
//                         else form(keys.contactAddress.ukContactAddress.addressLine4.key).value
//
//    val ukAddressLine5 = if (adaptedAddressList.size > 4) Some(adaptedAddressList(4))
//                         else form(keys.contactAddress.ukContactAddress.addressLine5.key).value
//
//    val ukAddressPostcode = if (ukAddress.isDefined) Some(ukAddress.get.postcode)
//                            else form(keys.contactAddress.ukContactAddress.postcode.key).value

    val ukContactAddressModel = UKContactAddressModel(
      ukAddressOption = RadioField(
        key = keys.contactAddress.contactAddressType,
        value = "uk"
      ),
      ukAddressLineText = HiddenField (
        key = keys.contactAddress.ukAddressTextLine,
        value =  ukAddressToBeShown.getOrElse("")
      )
//      ,
//
//      ukAddressLine1 = HiddenField(
//        key = keys.contactAddress.ukContactAddress.addressLine1,
//        value =  ukAddressLine1.getOrElse("")
//      ),
//      ukAddressLine2 = HiddenField(
//        key = keys.contactAddress.ukContactAddress.addressLine2,
//        value =  ukAddressLine2.getOrElse("")
//      ),
//      ukAddressLine3 = HiddenField(
//        key = keys.contactAddress.ukContactAddress.addressLine3,
//        value =  ukAddressLine3.getOrElse("")
//      ),
//      ukAddressLine4 = HiddenField(
//        key = keys.contactAddress.ukContactAddress.addressLine4,
//        value =  ukAddressLine4.getOrElse("")
//      ),
//      ukAddressLine5 = HiddenField(
//        key = keys.contactAddress.ukContactAddress.addressLine5,
//        value =  ukAddressLine5.getOrElse("")
//      ),
//      ukAddressPostcode = HiddenField(
//        key = keys.contactAddress.ukContactAddress.postcode,
//        value =  ukAddressPostcode.getOrElse("")
//      )
    )

    val bfpoContactAddressModel = BFPOContactAddressModel (
      BFPOAddressOption = RadioField(
        key = keys.contactAddress.contactAddressType,
        value = "bfpo"
      ),
      BFPOAddressLine1 = TextField(
        key = keys.contactAddress.bfpoContactAddress.addressLine1
      ),
      BFPOAddressLine2 = TextField(
        key = keys.contactAddress.bfpoContactAddress.addressLine2
      ),
      BFPOAddressLine3 = TextField(
        key = keys.contactAddress.bfpoContactAddress.addressLine3
      ),
      BFPOAddressLine4 = TextField(
        key = keys.contactAddress.bfpoContactAddress.addressLine4
      ),
      BFPOAddressLine5 = TextField(
        key = keys.contactAddress.bfpoContactAddress.addressLine5
      ),
      BFPOAddressPostcode = TextField(
        key = keys.contactAddress.bfpoContactAddress.postcode
      )
    )

    val otherContactAddressModel = OtherContactAddressModel(
      otherAddressOption = RadioField(
        key = keys.contactAddress.contactAddressType,
        value = "other"
      ),
      otherAddressLine1 = TextField(
        key = keys.contactAddress.otherContactAddress.addressLine1
      ),
      otherAddressLine2 = TextField(
        key = keys.contactAddress.otherContactAddress.addressLine2
      ),
      otherAddressLine3 = TextField(
        key = keys.contactAddress.otherContactAddress.addressLine3
      ),
      otherAddressLine4 = TextField(
        key = keys.contactAddress.otherContactAddress.addressLine4
      ),
      otherAddressLine5 = TextField(
        key = keys.contactAddress.otherContactAddress.addressLine5
      ),
      otherAddressPostcode = TextField(
        key = keys.contactAddress.otherContactAddress.postcode
      ),
      otherAddressCountry = TextField(
        key = keys.contactAddress.otherContactAddress.country
      )
    )

    ContactAddressModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map (_.url).getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "9",
        title = "Where should we write to you about your registration?"
      ),
      contactAddressFieldSet = FieldSet (
        classes = if (form(keys.contactAddress.key).hasErrors) "invalid" else ""
      ),
      ukAddress = ukContactAddressModel,
      bfpoAddress = bfpoContactAddressModel,
      otherAddress = otherContactAddressModel
    )
  }


  private def extractUkAddressText(address: Option[PartialAddress], form: ErrorTransformForm[InprogressForces]): Option[String] = {
    if (address.isDefined) {
      val addressLine = address.map(_.addressLine).getOrElse(None)
      addressLine match {
        case None => address.get.manualAddress
        case _ => addressLine
      }
    }
    else form(keys.contactAddress.ukAddressLine.key).value
  }


//  private def extractUkAddressLine(application: Option[InprogressForces], form: ErrorTransformForm[InprogressForces], lineFormKey: String): Option[String] = {
//    if (application.isDefined) {
//      val ukAddress = application.get.address
//        if (ukAddress.isDefined) {
//          val addressLine = ukAddress.get.addressLine
//          if (addressLine.isDefined)  {
//            application.copy(contactAddress =
//              Some(updateContactAddressWithAdressLines(addressLine, application.contactAddress.get))
//            )
//          }
//          else application
//        }
//        else None
//
//    }
//    else form(lineFormKey).value
//  }

//  private def updateContactAddressWithAdressLines(addressLine: Option[String], currentContactAddress: ContactAddress): ContactAddress = {
//    val adaptedAddressList: List[String] = splitAddressLineIntoList(addressLine)
//    currentContactAddress.copy(
//      addressLine1 = if (adaptedAddressList.size > 0) Some(adaptedAddressList(0)) else None,
//      addressLine2 = if (adaptedAddressList.size > 1) Some(adaptedAddressList(1)) else None,
//      addressLine3 = if (adaptedAddressList.size > 2) Some(adaptedAddressList(2)) else None,
//      addressLine4 = if (adaptedAddressList.size > 3) Some(adaptedAddressList(3)) else None,
//      addressLine5 = if (adaptedAddressList.size > 4) Some(adaptedAddressList(4)) else None
//    )
//  }

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

  def contactAddressMustache(
        form:ErrorTransformForm[InprogressForces],
        post: Call,
        back: Option[Call]): Html = {
      
    val data = transformFormStepToMustacheData(form, post, back)
    val content = Mustache.render("forces/contactAddress", data)
    MainStepTemplate(content, data.question.title)
  }
}
