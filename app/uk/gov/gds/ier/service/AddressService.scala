package uk.gov.gds.ier.service

import uk.gov.gds.ier.model.{Address, PartialAddress}
import com.google.inject.Inject

class AddressService @Inject()(placesService: PlacesService) {

  def formFullAddress(partial:Option[PartialAddress]):Option[Address] = {
    partial flatMap {
      case PartialAddress(_, Some(uprn), postcode, _) => {
        val listOfAddresses = placesService.lookupAddress(postcode)
        listOfAddresses.find(address => address.uprn == Some(uprn))
      }
      case PartialAddress(_, None, postcode, Some(manualAddress)) => {
        Some(Address(
          lineOne = manualAddress.lineOne,
          lineTwo = manualAddress.lineTwo,
          lineThree = manualAddress.lineThree,
          city = manualAddress.city,
          county = None,
          uprn = None,
          postcode = postcode))
      }
    }
  }

  def lookupPartialAddress(postcode:String):List[PartialAddress] = {
    placesService.lookupAddress(postcode) map { address =>
      PartialAddress(
        addressLine = Some(formAddressLine(address)),
        uprn = address.uprn,
        postcode = address.postcode,
        None
      )
    }
  }

  def fillAddressLine(partial:PartialAddress):PartialAddress = {
    val line = placesService.lookupAddress(partial) map formAddressLine
    partial.copy(addressLine = line)
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
}
