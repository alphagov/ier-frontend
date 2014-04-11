package uk.gov.gds.ier.transaction.crown.address

import uk.gov.gds.ier.service.AddressService

trait WithAddressService {
  val addressService: AddressService
}
