package uk.gov.gds.ier.test

import uk.gov.gds.ier.service.ScotlandService
import uk.gov.gds.ier.model.Country

trait WithMockScotlandService {

  private val mockito = new MockitoHelpers {}

  val scotlandService = {

    val mockService = mockito.mock[ScotlandService]
    mockito.when (mockService.isScotByPostcodeOrCountry("EH11QN", new Country("Scotland", false))).thenReturn(true)
    mockito.when (mockService.isScotByPostcodeOrCountry("L77AJ", new Country("England", false))).thenReturn(false)
    mockito.when (mockService.isScotByPostcodeOrCountry("", new Country("Scotland", false))).thenReturn(true)
    mockito.when (mockService.isScotByPostcodeOrCountry("", new Country("England", false))).thenReturn(false)
    mockService
  }

}
