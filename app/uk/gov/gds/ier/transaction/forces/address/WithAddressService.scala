package uk.gov.gds.ier.transaction.forces.address

import uk.gov.gds.ier.service.AddressService

trait WithAddressService {
  val addressService:AddressService
}
