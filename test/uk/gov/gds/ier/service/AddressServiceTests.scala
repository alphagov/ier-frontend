package uk.gov.gds.ier.service

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.{Matchers => MockitoMatchers}
import uk.gov.gds.ier.model.{PartialManualAddress, Address, PartialAddress}

class AddressServiceTests extends FlatSpec
  with Matchers
  with TestHelpers
  with MockitoSugar {

  behavior of "AddressService.formFullAddress"

  it should "perform a lookup against places when a uprn is provided" in {
    val mockPlaces = mock[PlacesService]
    val service = new AddressService(mockPlaces)
    val partial = PartialAddress(
      addressLine = None,
      uprn = Some("12345"),
      postcode = "AB12 3CD",
      manualAddress = None)

    when(mockPlaces.lookupAddress("AB12 3CD")).thenReturn(List.empty)
    service.formFullAddress(Some(partial))
    verify(mockPlaces).lookupAddress("AB12 3CD")
  }

  it should "pick the correct address out of the returned list" in {
    val mockPlaces = mock[PlacesService]
    val service = new AddressService(mockPlaces)
    val partial = PartialAddress(
      addressLine = None,
      uprn = Some("12345"),
      postcode = "AB12 3CD",
      manualAddress = None
    )
    val address = Address(
      lineOne = Some("123 Fake Street"),
      lineTwo = None,
      lineThree = None,
      city = Some("Fakerton"),
      county = Some("Fakesbury"),
      uprn = Some("12345"),
      postcode = "AB12 3CD"
    )

    when(mockPlaces.lookupAddress("AB12 3CD")).thenReturn(List(address))
    service.formFullAddress(Some(partial)) should be(Some(address))
    verify(mockPlaces).lookupAddress("AB12 3CD")
  }

  it should "provide a manual address formed when no uprn provided" in {
    val mockPlaces = mock[PlacesService]
    val service = new AddressService(mockPlaces)
    val partial = PartialAddress(
      addressLine = None,
      uprn = None,
      postcode = "AB12 3CD",
      manualAddress = Some(PartialManualAddress(
        lineOne = Some("123 Fake Street"),
        city = Some("Fakerton")))
    )
    val address = Address(
      lineOne = Some("123 Fake Street"),
      lineTwo = None,
      lineThree = None,
      city = Some("Fakerton"),
      county = None,
      uprn = None,
      postcode = "AB12 3CD"
    )

    service.formFullAddress(Some(partial)) should be(Some(address))
    verify(mockPlaces, never()).lookupAddress("AB12 3CD")
  }

  it should "return none if no partial provided" in {
    val mockPlaces = mock[PlacesService]
    val service = new AddressService(mockPlaces)

    service.formFullAddress(None) should be(None)
    verify(mockPlaces, never()).lookupAddress(MockitoMatchers.anyString())
  }

  behavior of "AddressService.formAddressLine"

  it should "combine the 3 lines correctly" in {
    val mockPlaces = mock[PlacesService]
    val service = new AddressService(mockPlaces)

    val address = Address(
      lineOne = Some("1A Fake Flat"),
      lineTwo = Some("Fake House"),
      lineThree = Some("123 Fake Street"),
      city = Some("Fakerton"),
      county = Some("Fakesbury"),
      uprn = Some("12345678"),
      postcode = "AB12 3CD")

    service.formAddressLine(address) should be(
      "1A Fake Flat, Fake House, 123 Fake Street, Fakerton, Fakesbury"
    )
  }

  it should "filter out Nones" in {
    val mockPlaces = mock[PlacesService]
    val service = new AddressService(mockPlaces)

    val address = Address(
      lineOne = Some("1A Fake Flat"),
      lineTwo = None,
      lineThree = None,
      city = Some("Fakerton"),
      county = Some("Fakesbury"),
      uprn = Some("12345678"),
      postcode = "AB12 3CD")

    service.formAddressLine(address) should be(
      "1A Fake Flat, Fakerton, Fakesbury"
    )
  }


  it should "filter out empty strings" in {
    val mockPlaces = mock[PlacesService]
    val service = new AddressService(mockPlaces)

    val address = Address(
      lineOne = Some("1A Fake Flat"),
      lineTwo = Some(""),
      lineThree = Some(""),
      city = Some("Fakerton"),
      county = Some("Fakesbury"),
      uprn = Some("12345678"),
      postcode = "AB12 3CD")

    service.formAddressLine(address) should be(
      "1A Fake Flat, Fakerton, Fakesbury"
    )
  }
}
