package uk.gov.gds.ier.service

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.{Matchers => MockitoMatchers}
import uk.gov.gds.ier.model.{PartialManualAddress, Address, PartialAddress, LastUkAddress}

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

  it should "provide correct address containing only a postcode (NI case)" in {
    val mockPlaces = mock[LocateService]
    val service = new AddressService(mockPlaces)
    val partial = PartialAddress(
      addressLine = None,
      uprn = None,
      postcode = "BT7 1AA",
      manualAddress = None,
      gssCode = None
    )
    val address = Address(
      lineOne = None,
      lineTwo = None,
      lineThree = None,
      city = None,
      county = None,
      uprn = None,
      postcode = "BT7 1AA",
      gssCode = None
    )

    service.formFullAddress(Some(partial)) should be(Some(address))
    verify(mockPlaces, never()).lookupAddress("BT7 1AA")
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

  behavior of "isScotland that is when testing Scotland address"
  it should "return positive on address with scottish postcode" in {
    val mockLocate = mock[LocateService]
    val addressService = new AddressService(mockLocate)
    val scottishSampleAddress = Address(postcode = "BBB11 2BB").copy(gssCode = Some("S123456789"))
    when(mockLocate.lookupAddress("BBB11 2BB")).thenReturn(List(scottishSampleAddress))

    addressService.isScotland(postcode = "BBB11 2BB") should be(true)
  }

  it should "return negative on address with english postcode" in {
    val mockLocate = mock[LocateService]
    val addressService = new AddressService(mockLocate)
    val englishSampleAddress  = Address(postcode = "AAA22 1AA").copy(gssCode = Some("E998989654"))
    when(mockLocate.lookupAddress("AAA22 1AA")).thenReturn(List(englishSampleAddress))

    addressService.isScotland(postcode = "AAA22 1AA") should be(false)
  }

  it should "return negative on address with postcode returning empty list" in {
    val mockLocate = mock[LocateService]
    val addressService = new AddressService(mockLocate)
    when(mockLocate.lookupAddress("CCC33 3CC")).thenReturn(List())

    addressService.isScotland("CCC33 3CC") should be(false)
  }

  behavior of "isNorthernIreland"
  it should "positively identify Northern Irish post code" in {
    val mockLocate = mock[LocateService]
    val addressService = new AddressService(mockLocate)

    addressService.isNothernIreland(postcode = "BT7 1AA") should be(true)
    addressService.isNothernIreland(postcode = "bt71aa") should be(true)
    addressService.isNothernIreland(postcode = "  BT7 1AA  ") should be(true)
    addressService.isNothernIreland(postcode = "   bt71aa ") should be(true)

    addressService.isNothernIreland(postcode = "SW1 E34") should be(false)
    addressService.isNothernIreland(postcode = "NU2 6UN") should be(false)
    addressService.isNothernIreland(postcode = "ABC DEF") should be(false)
    addressService.isNothernIreland(postcode = "ABCDEF") should be(false)
    addressService.isNothernIreland(postcode = "abcdef") should be(false)


  }

}
