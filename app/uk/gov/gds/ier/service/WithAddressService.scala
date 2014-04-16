package uk.gov.gds.ier.service

import uk.gov.gds.ier.service.AddressService

trait WithAddressService {
  val addressService: AddressService
}