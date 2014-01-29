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
    println ("errors => " + globalErrors)
    val possibleAddressesForm = form.value.flatMap{ application => application.possibleAddresses}
    val addressForm = form.value.flatMap{ application => application.address}
    implicit val progressForm = form
    
    val listAddress = form(keys.possibleAddresses.jsonList.key).value match {
        case Some(addresses) => (serialiser.fromJson[Addresses] (addresses)).addresses
        case _ => Nil
    }
    
    val possibleAddressesField = Field(
            name = keys.possibleAddresses.postcode.key, 
            id = keys.possibleAddresses.postcode.asId(), 
            value = form(keys.possibleAddresses.postcode.key).value.getOrElse(""),
            classes = if (form(keys.possibleAddresses.postcode.key).hasErrors || (form(keys.possibleAddresses.postcode.key).value != None && listAddress.isEmpty)) "invalid" else "")
    val possibleAddressesJsonListField = TextField(key = keys.possibleAddresses.jsonList) 
    val addressPostcodeField = TextField (key = keys.address.postcode)
    val addressUprnField =  Field (
            name = keys.address.uprn.key, 
            id = keys.address.uprn.asId("select"), 
            value = form(keys.address.uprn.key).value.getOrElse(""),
            classes = if (form(keys.address.uprn.key).hasErrors) "invalid" else "")
    val addressManualAddressField = TextField(key = keys.address.manualAddress)
            
    def data = AddressModel (
            question = Question(
              postUrl = post.url, backUrl = back.map{call => call.url}.getOrElse(""),
              errorMessages = globalErrors.map(_.message),
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
