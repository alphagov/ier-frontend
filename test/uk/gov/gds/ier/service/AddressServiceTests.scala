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
    val mockPlaces = mock[LocateService]
    val service = new AddressService(mockPlaces)
    val partial = PartialAddress(
      addressLine = None,
      uprn = Some("12345"),
      postcode = "AB12 3CD",
      manualAddress = None,
      gssCode = Some("abc"))

    when(mockPlaces.lookupAddress("AB12 3CD")).thenReturn(List.empty)
    service.formFullAddress(Some(partial))
    verify(mockPlaces).lookupAddress("AB12 3CD")
  }

  it should "pick the correct address out of the returned list" in {
    val mockPlaces = mock[LocateService]
    val service = new AddressService(mockPlaces)
    val partial = PartialAddress(
      addressLine = None,
      uprn = Some("12345"),
      postcode = "AB12 3CD",
      manualAddress = None,
      gssCode = Some("abc")
    )
    val address = Address(
      lineOne = Some("123 Fake Street"),
      lineTwo = None,
      lineThree = None,
      city = Some("Fakerton"),
      county = Some("Fakesbury"),
      uprn = Some("12345"),
      postcode = "AB12 3CD",
      gssCode = Some("abc")
    )

    when(mockPlaces.lookupAddress("AB12 3CD")).thenReturn(List(address))
    service.formFullAddress(Some(partial)) should be(Some(address))
    verify(mockPlaces).lookupAddress("AB12 3CD")
  }

  it should "provide a manual address formed when no uprn provided" in {
    val mockPlaces = mock[LocateService]
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
    val mockPlaces = mock[LocateService]
    val service = new AddressService(mockPlaces)

    service.formFullAddress(None) should be(None)
    verify(mockPlaces, never()).lookupAddress(MockitoMatchers.anyString())
  }

  behavior of "AddressService.formAddressLine"

  it should "combine the 3 lines correctly" in {
    val mockPlaces = mock[LocateService]
    val service = new AddressService(mockPlaces)

    val address = Address(
      lineOne = Some("1A Fake Flat"),
      lineTwo = Some("Fake House"),
      lineThree = Some("123 Fake Street"),
      city = Some("Fakerton"),
      county = Some("Fakesbury"),
      uprn = Some("12345678"),
      postcode = "AB12 3CD",
      gssCode = Some("abc"))

    service.formAddressLine(address) should be(
      "1A Fake Flat, Fake House, 123 Fake Street, Fakerton, Fakesbury"
    )
  }

  it should "filter out Nones" in {
    val mockPlaces = mock[LocateService]
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
    val mockPlaces = mock[LocateService]
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
  
  it should "return the PartialAddress with gssCode after partial address lookup" in {
    val mockLocate = mock[LocateService]
    val service = new AddressService(mockLocate)

    val partial = PartialAddress(
      addressLine = None,
      uprn = Some("12345678"),
      postcode = "AB12 3CD",
      manualAddress = None)
      
    val address = Address(
      lineOne = Some("1A Fake Flat"),
      lineTwo = Some(""),
      lineThree = Some(""),
      city = Some("Fakerton"),
      county = Some("Fakesbury"),
      uprn = Some("12345678"),
      postcode = "AB12 3CD",
      gssCode = Some("gss"))
      
    when(mockLocate.lookupAddress(partial)).thenReturn(Some(address))

    val result = partial.copy (addressLine = Some("1A Fake Flat, Fakerton, Fakesbury"),
        gssCode = Some("gss"))
    
    service.fillAddressLine(partial) should be(result)
  }
  
  it should "return true if the address lookup response with gssCode starts with 'S'" in {
    val mockLocate = mock[LocateService]
    val service = new AddressService(mockLocate)

    val postcode = "AB12 3CD"
      
    val address = Address(
      lineOne = Some("1A Fake Flat"),
      lineTwo = Some(""),
      lineThree = Some(""),
      city = Some("Fakerton"),
      county = Some("Fakesbury"),
      uprn = Some("12345678"),
      postcode = "AB12 3CD",
      gssCode = Some("S123"))
      
    when(mockLocate.lookupAddress(postcode)).thenReturn(List(address))

    service.isScotland(postcode) should be(true)
  }
  
  it should "return false if the address lookup response with gssCode doesn't start with 'S'" in {
    val mockLocate = mock[LocateService]
    val service = new AddressService(mockLocate)

    val postcode = "AB12 3CD"
      
    val address = Address(
      lineOne = Some("1A Fake Flat"),
      lineTwo = Some(""),
      lineThree = Some(""),
      city = Some("Fakerton"),
      county = Some("Fakesbury"),
      uprn = Some("12345678"),
      postcode = "AB12 3CD",
      gssCode = Some("A123"))
      
    when(mockLocate.lookupAddress(postcode)).thenReturn(List(address))

    service.isScotland(postcode) should be(false)
  }
}
