package uk.gov.gds.ier.transaction.address

import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.{InprogressOrdinary, Address, PartialAddress}
import uk.gov.gds.ier.mustache.StepMustache
import play.api.libs.json.Json
import play.api.libs.json.JsPath
import play.api.libs.json.Reads
import play.api.libs.json.JsSuccess
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.PossibleAddress
import uk.gov.gds.ier.model.Addresses
import uk.gov.gds.ier.logging.Logging

trait AddressMustache extends StepMustache with WithSerialiser with Logging{

  case class AddressModel(
          question: Question,
          addressLookUpUrl: String = "",
          possibleAddresses: Field, 
          possibleAddressesJsonList: Field,
          possibleAddressesJsonListObject: List[PartialAddress],
          listAddressSize: Int,
          addressPostcode: Field,
          addressUprnSelect: Field,
          addressManualAddress: Field)

  def transformFormStepToMustacheData (form:ErrorTransformForm[InprogressOrdinary], post:Call, back: Option[Call]): Html = {
    val globalErrors = form.globalErrors 
    val possibleAddressesForm = form.value.flatMap{ application => application.possibleAddresses}
    val addressForm = form.value.flatMap{ application => application.address}
    
    val possibleAddressesField = Field(
            name = keys.possibleAddresses.postcode.key, 
            id = keys.possibleAddresses.postcode.asId(), 
            value = form(keys.possibleAddresses.postcode.key).value.getOrElse(""),
            classes = if (form(keys.possibleAddresses.postcode.key).hasErrors) "invalid" else "")
    val possibleAddressesJsonListField = Field(
            name = keys.possibleAddresses.jsonList.key, 
            id = keys.possibleAddresses.jsonList.asId(), 
            value = form(keys.possibleAddresses.jsonList.key).value.getOrElse(""))
    val addressPostcodeField = Field (
            name = keys.address.postcode.key, 
            id = keys.address.postcode.asId(), 
            value = form(keys.address.postcode.key).value.getOrElse(""))
    val addressUprnField = Field (
            name = keys.address.uprn.key, 
            id = keys.address.uprn.asId("select"), 
            value = form(keys.address.uprn.key).value.getOrElse(""),
            classes = if (form(keys.address.uprn.key).hasErrors) "invalid" else "")
    val addressManualAddressField = Field (
            name = keys.address.manualAddress.key, 
            id = keys.address.manualAddress.asId(), 
            value = form(keys.address.manualAddress.key).value.getOrElse(""))
            
    val listAddress = form(keys.possibleAddresses.jsonList.key).value match {
        case Some("") => Nil
        case Some(addresses) => (serialiser.fromJson[Addresses] (addresses)).addresses
        case None => Nil
    }
    
    val listAddressError = form(keys.possibleAddresses.postcode.key).value match {
        case Some(postcode) => if (postcode.trim != "" && listAddress.isEmpty && globalErrors.size == 0) List("Please enter a valid postcode") else Nil 
        case None => Nil
    }
    
    def data = AddressModel (
            question = Question(
              postUrl = post.url, backUrl = back.map{call => call.url}.getOrElse(""),
              errorMessages = listAddressError ++ globalErrors.map(_.message),
              number = "6 of 11",
              title = "Where do you live?"
            ),
            post.url + "/lookup", 
            possibleAddresses = possibleAddressesField,
            possibleAddressesJsonList = possibleAddressesJsonListField,
            possibleAddressesJsonListObject = listAddress, 
            listAddressSize = listAddress.size,
            addressPostcode = addressPostcodeField,
            addressUprnSelect = addressUprnField,
            addressManualAddress = addressManualAddressField
    )

    val content = Mustache.render("ordinary/address", data)
    MainStepTemplate(content, data.question.title)
  }
}
