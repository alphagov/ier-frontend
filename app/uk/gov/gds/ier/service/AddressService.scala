package uk.gov.gds.ier.service

import uk.gov.gds.ier.model.{Address, PartialAddress}
import com.google.inject.Inject

class AddressService @Inject()(locateService: LocateService) {

  def formFullAddress(partial:Option[PartialAddress]):Option[Address] = {
    partial flatMap {
      case PartialAddress(_, Some(uprn), postcode, _, _) => {
        val listOfAddresses = locateService.lookupAddress(postcode)
        listOfAddresses.find(address => address.uprn == Some(uprn))
      }
      case PartialAddress(_, None, postcode, Some(manualAddress), gssCode) => {
        Some(Address(
          lineOne = manualAddress.lineOne,
          lineTwo = manualAddress.lineTwo,
          lineThree = manualAddress.lineThree,
          city = manualAddress.city,
          county = None,
          uprn = None,
          postcode = postcode,
          gssCode = gssCode))
      }
      case PartialAddress(_,_,postcode,_,_) => {
        Some(Address(
          lineOne = None,
          lineTwo = None,
          lineThree = None,
          city = None,
          county = None,
          uprn = None,
          postcode = postcode,
          gssCode = None
        ))
      }
    }
  }

  def lookupPartialAddress(postcode:String):List[PartialAddress] = {
    locateService.lookupAddress(postcode) map { address =>
      PartialAddress(
        addressLine = Some(formAddressLine(address)),
        uprn = address.uprn,
        postcode = address.postcode,
        None,
        gssCode = address.gssCode
      )
    }
  }

  def fillAddressLine(partial:PartialAddress):PartialAddress = {
    val address = locateService.lookupAddress(partial)
    val line = address map formAddressLine

    partial.copy(addressLine = line, gssCode = address.flatMap(_.gssCode))
  }

  def isScotland(postcode: String): Boolean = {
    locateService.lookupAddress(postcode).exists(_.gssCode.exists(_.startsWith("S")))
  }

  def isNothernIreland(postcode: String): Boolean = {
    postcode.trim.toUpperCase.startsWith("BT")
  }

  protected[service] def formAddressLine(address:Address):String = {
    List(
      address.lineOne,
      address.lineTwo,
      address.lineThree,
      address.city,
      address.county
    ).filterNot(line => line.map(_.replaceAllLiterally(" ","")) == Some(""))
      .flatten
      .mkString(", ")
  }

  /**
   * If GssCode is present in the Address, use it, if not use postcode and do locateService lookup
   * and pick first available gssCode from the results.
   */
  def gssCodeFor(address: PartialAddress): Option[String] = {
    if (address.gssCode.isDefined) {
      address.gssCode
    } else {
      locateService.lookupAddress(address.postcode).find(_.gssCode.isDefined).flatMap(_.gssCode)
    }
  }
}
