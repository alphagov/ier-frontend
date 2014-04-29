package uk.gov.gds.ier.transaction.crown.declaration

import uk.gov.gds.ier.service.PlacesService

trait WithPlacesService {
  val placesService: PlacesService
}
