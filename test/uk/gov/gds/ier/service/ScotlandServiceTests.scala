package uk.gov.gds.ier.service

import uk.gov.gds.ier.test.{WithMockConfig, MockingTestSuite}
import uk.gov.gds.ier.model._
import org.joda.time.LocalDate
import uk.gov.gds.ier.model.DateOfBirth
import uk.gov.gds.ier.model.Country
import uk.gov.gds.ier.model.DOB
import scala.Some
import uk.gov.gds.ier.validation.constants.DateOfBirthConstants

class ScotlandServiceTests extends MockingTestSuite with WithMockConfig {

  behavior of "ScotlandService.isScotByPostcodeOrCountry"

  it should "provide an accurate true / false responses for an isScotByPostcodeOrCountry check" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)

    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)

    service.isScotByPostcodeOrCountry("EH11QN", new Country("Scotland", false)) should be (true)
    service.isScotByPostcodeOrCountry("L77AJ", new Country("England", false)) should be (false)
    service.isScotByPostcodeOrCountry("", new Country("Scotland", false)) should be (true)
    service.isScotByPostcodeOrCountry("", new Country("England", false)) should be (false)

  }

  behavior of "ScotlandService.isUnderageScot"

  it should "return TRUE for a 15yr old application" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    val currentState = completeOrdinaryApplicationYoungScot.copy()

    service.isUnderageScot(currentState) should be (true)
  }

  it should "return TRUE for a 14yr old application" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      dob = Some(DateOfBirth(
        dob = Some(DOB(LocalDate.now.minusYears(14).getYear, 1, 1)),
        noDob = None
      ))
    )

    service.isUnderageScot(currentState) should be (true)
  }

  it should "return FALSE for a 13yr old application" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      dob = Some(DateOfBirth(
        dob = Some(DOB(LocalDate.now.minusYears(13).getYear, 1, 1)),
        noDob = None
      ))
    )

    service.isUnderageScot(currentState) should be (false)
  }

  it should "return FALSE for a 16yr old application" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      dob = Some(DateOfBirth(
        dob = Some(DOB(LocalDate.now.minusYears(16).getYear, 1, 1)),
        noDob = None
      ))
    )

    service.isUnderageScot(currentState) should be (false)
  }

  it should "return FALSE for a 13yr & 364d old application" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      dob = Some(DateOfBirth(
        dob = Some(DOB(
          LocalDate.now.minusYears(13).getYear,
          LocalDate.now.getMonthOfYear,
          LocalDate.now.plusDays(364).getDayOfYear)
        ),
        noDob = None
      ))
    )

    service.isUnderageScot(currentState) should be (false)
  }

  it should "return FALSE for a 16yr & 1d old application" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      dob = Some(DateOfBirth(
        dob = Some(DOB(
          LocalDate.now.minusYears(16).getYear,
          LocalDate.now.getMonthOfYear,
          LocalDate.now.plusDays(1).getDayOfYear)
        ),
        noDob = None
      ))
    )

    service.isUnderageScot(currentState) should be (false)
  }

  behavior of "ScotlandService.isScot"

  it should "return TRUE for an application with ADDRESS=None, Country=Scotland" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      address = None
    )

    service.isScot(currentState) should be (true)
  }

  it should "return FALSE for an application with ADDRESS=None, Country=England" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      address = None,
      country = Some(Country("England", false))
    )

    service.isScot(currentState) should be (false)
  }

  it should "return TRUE for an application with ADDRESS in SCOTLAND, Country=None" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      country = None
    )

    service.isScot(currentState) should be (true)
  }

  it should "return FALSE for an application with ADDRESS in ENGLAND, Country=None" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      address = Some(PartialAddress(
        addressLine = Some("123 Fake Street, Fakerton, Fakeville"),
        uprn =  Some("1234567890"),
        postcode = "L77AJ",
        manualAddress = None,
        gssCode = None
      )),
      country = None
    )

    service.isScot(currentState) should be (false)
  }

  it should "return TRUE for an application with ADDRESS in SCOTLAND, Country=SCOTLAND" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy()

    service.isScot(currentState) should be (true)
  }

  it should "return FALSE for an application with ADDRESS in ENGLAND, Country=ENGLAND" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      address = Some(PartialAddress(
        addressLine = Some("123 Fake Street, Fakerton, Fakeville"),
        uprn =  Some("1234567890"),
        postcode = "L77AJ",
        manualAddress = None,
        gssCode = None
      )),
      country = Some(Country("England", false))
    )

    service.isScot(currentState) should be (false)
  }

  it should "return FALSE for an application with ADDRESS=None, Country=None" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      address = None,
      country = None
    )

    service.isScot(currentState) should be (false)
  }

  behavior of "ScotlandService.isYoungScot"

  it should "return TRUE for an application with ADDRESS=None, Country=Scotland, DOB=young" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      address = None
    )

    service.isYoungScot(currentState) should be (true)
  }

  it should "return FALSE for an application with ADDRESS=None, Country=England, DOB=young" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      address = None,
      country = Some(Country("England", false))
    )

    service.isYoungScot(currentState) should be (false)
  }

  it should "return TRUE for an application with ADDRESS in SCOTLAND, Country=None, DOB=young" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      country = None
    )

    service.isYoungScot(currentState) should be (true)
  }

  it should "return FALSE for an application with ADDRESS in ENGLAND, Country=None, DOB=young" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      address = Some(PartialAddress(
        addressLine = Some("123 Fake Street, Fakerton, Fakeville"),
        uprn =  Some("1234567890"),
        postcode = "L77AJ",
        manualAddress = None,
        gssCode = None
      )),
      country = None
    )

    service.isYoungScot(currentState) should be (false)
  }

  it should "return TRUE for an application with ADDRESS in SCOTLAND, Country=SCOTLAND, DOB=young" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy()

    service.isYoungScot(currentState) should be (true)
  }

  it should "return FALSE for an application with ADDRESS in ENGLAND, Country=ENGLAND, DOB=young" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      address = Some(PartialAddress(
        addressLine = Some("123 Fake Street, Fakerton, Fakeville"),
        uprn =  Some("1234567890"),
        postcode = "L77AJ",
        manualAddress = None,
        gssCode = None
      )),
      country = Some(Country("England", false))
    )

    service.isYoungScot(currentState) should be (false)
  }

  it should "return FALSE for an application with ADDRESS=None, Country=None, DOB=young" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      address = None,
      country = None
    )

    service.isYoungScot(currentState) should be (false)
  }

  it should "return FALSE for an application with ADDRESS=None, Country=Scotland, DOB=old" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      address = None,
      dob = Some(DateOfBirth(
        dob = Some(DOB(LocalDate.now.minusYears(20).getYear, 1, 1)),
        noDob = None
      ))
    )

    service.isYoungScot(currentState) should be (false)
  }

  it should "return FALSE for an application with ADDRESS=None, Country=England, DOB=old" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      address = None,
      country = Some(Country("England", false)),
      dob = Some(DateOfBirth(
        dob = Some(DOB(LocalDate.now.minusYears(20).getYear, 1, 1)),
        noDob = None
      ))
    )

    service.isYoungScot(currentState) should be (false)
  }

  it should "return FALSE for an application with ADDRESS in SCOTLAND, Country=None, DOB=old" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      country = None,
      dob = Some(DateOfBirth(
        dob = Some(DOB(LocalDate.now.minusYears(20).getYear, 1, 1)),
        noDob = None
      ))
    )

    service.isYoungScot(currentState) should be (false)
  }

  it should "return FALSE for an application with ADDRESS in ENGLAND, Country=None, DOB=old" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      address = Some(PartialAddress(
        addressLine = Some("123 Fake Street, Fakerton, Fakeville"),
        uprn =  Some("1234567890"),
        postcode = "L77AJ",
        manualAddress = None,
        gssCode = None
      )),
      country = None,
      dob = Some(DateOfBirth(
        dob = Some(DOB(LocalDate.now.minusYears(20).getYear, 1, 1)),
        noDob = None
      ))
    )

    service.isYoungScot(currentState) should be (false)
  }

  it should "return FALSE for an application with ADDRESS in SCOTLAND, Country=SCOTLAND, DOB=old" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      dob = Some(DateOfBirth(
        dob = Some(DOB(LocalDate.now.minusYears(20).getYear, 1, 1)),
        noDob = None
      ))
    )

    service.isYoungScot(currentState) should be (false)
  }

  it should "return FALSE for an application with ADDRESS in ENGLAND, Country=ENGLAND, DOB=old" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      address = Some(PartialAddress(
        addressLine = Some("123 Fake Street, Fakerton, Fakeville"),
        uprn =  Some("1234567890"),
        postcode = "L77AJ",
        manualAddress = None,
        gssCode = None
      )),
      country = Some(Country("England", false)),
      dob = Some(DateOfBirth(
        dob = Some(DOB(LocalDate.now.minusYears(20).getYear, 1, 1)),
        noDob = None
      ))
    )

    service.isYoungScot(currentState) should be (false)
  }

  it should "return FALSE for an application with ADDRESS=None, Country=None, DOB=old" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      address = None,
      country = None,
      dob = Some(DateOfBirth(
        dob = Some(DOB(LocalDate.now.minusYears(20).getYear, 1, 1)),
        noDob = None
      ))
    )

    service.isYoungScot(currentState) should be (false)
  }

  behavior of "ScotlandService.resetNoDOBRange"

  it should "return FALSE for an application with ADDRESS in ENGLAND, noDOB=ANY" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      address = Some(PartialAddress(
        addressLine = Some("123 Fake Street, Fakerton, Fakeville"),
        uprn =  Some("1234567890"),
        postcode = "L77AJ",
        manualAddress = None,
        gssCode = None
      )),
      country = Some(Country("England", false)),
      dob = Some(DateOfBirth(
        dob = None,
        noDob = Some(noDOB(
          reason = Some("test reason"),
          range = Some(DateOfBirthConstants.is18to75)
        ))
      ))
    )

    service.resetNoDOBRange(currentState) should be (false)
  }

  it should "return FALSE for an application with ADDRESS in SCOTLAND, noDOB=14to15" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      dob = Some(DateOfBirth(
        dob = None,
        noDob = Some(noDOB(
          reason = Some("test reason"),
          range = Some(DateOfBirthConstants.is14to15)
        ))
      ))
    )

    service.resetNoDOBRange(currentState) should be (false)
  }

  it should "return FALSE for an application with ADDRESS in SCOTLAND, noDOB=16to17" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      dob = Some(DateOfBirth(
        dob = None,
        noDob = Some(noDOB(
          reason = Some("test reason"),
          range = Some(DateOfBirthConstants.is16to17)
        ))
      ))
    )

    service.resetNoDOBRange(currentState) should be (false)
  }

  it should "return FALSE for an application with ADDRESS in SCOTLAND, noDOB=over18" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      dob = Some(DateOfBirth(
        dob = None,
        noDob = Some(noDOB(
          reason = Some("test reason"),
          range = Some(DateOfBirthConstants.over18)
        ))
      ))
    )

    service.resetNoDOBRange(currentState) should be (false)
  }

  it should "return FALSE for an application with ADDRESS in ENGLAND, noDOB=under18" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      address = Some(PartialAddress(
        addressLine = Some("123 Fake Street, Fakerton, Fakeville"),
        uprn =  Some("1234567890"),
        postcode = "L77AJ",
        manualAddress = None,
        gssCode = None
      )),
      country = Some(Country("England", false)),
      dob = Some(DateOfBirth(
        dob = None,
        noDob = Some(noDOB(
          reason = Some("test reason"),
          range = Some(DateOfBirthConstants.under18)
        ))
      ))
    )

    service.resetNoDOBRange(currentState) should be (false)
  }

  it should "return FALSE for an application with ADDRESS in ENGLAND, noDOB=18to75" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      address = Some(PartialAddress(
        addressLine = Some("123 Fake Street, Fakerton, Fakeville"),
        uprn =  Some("1234567890"),
        postcode = "L77AJ",
        manualAddress = None,
        gssCode = None
      )),
      country = Some(Country("England", false)),
      dob = Some(DateOfBirth(
        dob = None,
        noDob = Some(noDOB(
          reason = Some("test reason"),
          range = Some(DateOfBirthConstants.is18to75)
        ))
      ))
    )

    service.resetNoDOBRange(currentState) should be (false)
  }

  it should "return FALSE for an application with ADDRESS in ENGLAND, noDOB=Over75" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      address = Some(PartialAddress(
        addressLine = Some("123 Fake Street, Fakerton, Fakeville"),
        uprn =  Some("1234567890"),
        postcode = "L77AJ",
        manualAddress = None,
        gssCode = None
      )),
      country = Some(Country("England", false)),
      dob = Some(DateOfBirth(
        dob = None,
        noDob = Some(noDOB(
          reason = Some("test reason"),
          range = Some(DateOfBirthConstants.over75)
        ))
      ))
    )

    service.resetNoDOBRange(currentState) should be (false)
  }

  it should "return TRUE for an application with ADDRESS in ENGLAND, noDOB=14to15" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      address = Some(PartialAddress(
        addressLine = Some("123 Fake Street, Fakerton, Fakeville"),
        uprn =  Some("1234567890"),
        postcode = "L77AJ",
        manualAddress = None,
        gssCode = None
      )),
      country = Some(Country("England", false)),
      dob = Some(DateOfBirth(
        dob = None,
        noDob = Some(noDOB(
          reason = Some("test reason"),
          range = Some(DateOfBirthConstants.is14to15)
        ))
      ))
    )

    service.resetNoDOBRange(currentState) should be (true)
  }

  it should "return TRUE for an application with ADDRESS in ENGLAND, noDOB=16to17" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      address = Some(PartialAddress(
        addressLine = Some("123 Fake Street, Fakerton, Fakeville"),
        uprn =  Some("1234567890"),
        postcode = "L77AJ",
        manualAddress = None,
        gssCode = None
      )),
      country = Some(Country("England", false)),
      dob = Some(DateOfBirth(
        dob = None,
        noDob = Some(noDOB(
          reason = Some("test reason"),
          range = Some(DateOfBirthConstants.is16to17)
        ))
      ))
    )

    service.resetNoDOBRange(currentState) should be (true)
  }

  it should "return TRUE for an application with ADDRESS in ENGLAND, noDOB=over18" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      address = Some(PartialAddress(
        addressLine = Some("123 Fake Street, Fakerton, Fakeville"),
        uprn =  Some("1234567890"),
        postcode = "L77AJ",
        manualAddress = None,
        gssCode = None
      )),
      country = Some(Country("England", false)),
      dob = Some(DateOfBirth(
        dob = None,
        noDob = Some(noDOB(
          reason = Some("test reason"),
          range = Some(DateOfBirthConstants.over18)
        ))
      ))
    )

    service.resetNoDOBRange(currentState) should be (true)
  }

  it should "return TRUE for an application with ADDRESS in SCOTLAND, noDOB=under18" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      dob = Some(DateOfBirth(
        dob = None,
        noDob = Some(noDOB(
          reason = Some("test reason"),
          range = Some(DateOfBirthConstants.under18)
        ))
      ))
    )

    service.resetNoDOBRange(currentState) should be (true)
  }

  it should "return TRUE for an application with ADDRESS in SCOTLAND, noDOB=18to75" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      dob = Some(DateOfBirth(
        dob = None,
        noDob = Some(noDOB(
          reason = Some("test reason"),
          range = Some(DateOfBirthConstants.is18to75)
        ))
      ))
    )

    service.resetNoDOBRange(currentState) should be (true)
  }

  it should "return TRUE for an application with ADDRESS in SCOTLAND, noDOB=Over75" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      dob = Some(DateOfBirth(
        dob = None,
        noDob = Some(noDOB(
          reason = Some("test reason"),
          range = Some(DateOfBirthConstants.over75)
        ))
      ))
    )

    service.resetNoDOBRange(currentState) should be (true)
  }

  it should "return FALSE for an application with ADDRESS in SCOTLAND, noDOB=dontknow" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      dob = Some(DateOfBirth(
        dob = None,
        noDob = Some(noDOB(
          reason = Some("test reason"),
          range = Some(DateOfBirthConstants.dontKnow)
        ))
      ))
    )

    service.resetNoDOBRange(currentState) should be (false)
  }

  it should "return FALSE for an application with ADDRESS in ENGLAND, noDOB=dontknow" in {
    val mockAddressService = mock[AddressService]
    val service = getScotlandService(mockAddressService)
    when(mockAddressService.isScotAddress("EH11QN")).thenReturn(true)
    when(mockAddressService.isScotAddress("L77AJ")).thenReturn(false)
    when(mockAddressService.isScotAddress("")).thenReturn(false)
    val currentState = completeOrdinaryApplicationYoungScot.copy(
      address = Some(PartialAddress(
        addressLine = Some("123 Fake Street, Fakerton, Fakeville"),
        uprn =  Some("1234567890"),
        postcode = "L77AJ",
        manualAddress = None,
        gssCode = None
      )),
      country = Some(Country("England", false)),
      dob = Some(DateOfBirth(
        dob = None,
        noDob = Some(noDOB(
          reason = Some("test reason"),
          range = Some(DateOfBirthConstants.dontKnow)
        ))
      ))
    )

    service.resetNoDOBRange(currentState) should be (false)
  }

  private def getScotlandService(addressService: AddressService = mock[AddressService]) = {
    new ScotlandService(addressService)
  }
}
