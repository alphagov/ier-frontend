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
          postUrl:String = "", 
          addressLookUpUrl: String = "",
          possibleAddresses: Field, 
          possibleAddressesJsonList: Field,
          possibleAddressesJsonListObject: List[PartialAddress],
          listAddressSize: Int,
          addressPostcode: Field,
          addressUprnSelect: Field,
          addressManualAddress: Field,
          globalErrors:Seq[String] = List.empty )

  def transformFormStepToMustacheData (form:ErrorTransformForm[InprogressOrdinary], postUrl:String): AddressModel = {
    val globalErrors = form.globalErrors 
    val possibleAddressesForm = form.value.flatMap{ application => application.possibleAddresses}
    val addressForm = form.value.flatMap{ application => application.address}
    

    val possibleAddressesField = Field(
            name = keys.possibleAddresses.postcode.key, 
            id = keys.possibleAddresses.postcode.asId(), 
            value = form(keys.possibleAddresses.postcode.key).value.getOrElse(""))
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
            id = keys.address.uprn.asId(), 
            value = form(keys.address.uprn.key).value.getOrElse(""))
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
    
    AddressModel(postUrl, postUrl + "/lookup",
            possibleAddresses = possibleAddressesField,
            possibleAddressesJsonList = possibleAddressesJsonListField,
            possibleAddressesJsonListObject = listAddress, 
            listAddressSize = listAddress.size,
            addressPostcode = addressPostcodeField,
            addressUprnSelect = addressUprnField,
            addressManualAddress = addressManualAddressField,
            listAddressError ++ globalErrors.map(_.message) 
    )
  }

  def addressMustache(form: ErrorTransformForm[InprogressOrdinary], call:Call):Html = {
    val data = transformFormStepToMustacheData(form, call.url)
    val content = Mustache.render("ordinary/address", data)
    MainStepTemplate(content, "Register to Vote - Where do you live?")
  }
}
