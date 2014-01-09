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
          lineOne = Some(manualAddress),
          lineTwo = None,
          lineThree = None,
          city = None,
          county = None,
          uprn = None,
          postcode = postcode))
      }
    }
  }
}
