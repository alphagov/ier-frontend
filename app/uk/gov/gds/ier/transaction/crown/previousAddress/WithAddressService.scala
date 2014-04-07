package uk.gov.gds.ier.transaction.crown.previousAddress

import uk.gov.gds.ier.service.AddressService

trait WithAddressService {
  val addressService: AddressService
}
