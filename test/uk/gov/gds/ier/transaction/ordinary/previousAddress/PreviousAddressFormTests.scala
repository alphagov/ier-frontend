package uk.gov.gds.ier.transaction.ordinary.previousAddress

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.libs.json.Json
import uk.gov.gds.ier.model.{Addresses, PartialAddress}
import uk.gov.gds.ier.transaction.ordinary.address.AddressForms

class PreviousAddressFormTests
  extends FlatSpec
  with Matchers
  with AddressForms
  with PreviousAddressForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  // TST1
  it should "error out with empty movedRecently and click on 'Find address'" in {
    val js = Json.toJson(
      Map(
        "previousAddress.findAddress" -> "true"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("previousAddress.movedRecently") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
      },
      success => fail("Should have thrown an error")
    )
  }

  // TST2
  it should "error out with empty movedRecently and click on 'Continue'" in {
    val js = Json.toJson(
      Map(
        "previousAddress.findAddress" -> "false"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("previousAddress.movedRecently") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
      },
      success => fail("Should have thrown an error")
    )
  }

  // TST3
  it should "error out with movedRecently=false, empty postcode and click on 'Find address'" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "false",
        "previousAddress.findAddress" -> "true"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("previousAddress.movedRecently") should be(Seq("Please click 'Continue' if you haven't moved"))
        hasErrors.globalErrorMessages should be(Seq("Please click 'Continue' if you haven't moved"))
      },
      success => fail("Should have thrown an error")
    )
  }

  // TST4
  it should "successfully bind to movedRecently=false, empty postcode and click on 'Continue'" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "false",
        "previousAddress.findAddress" -> "false"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.previousAddress.isDefined should be(true)
        val previousAddressWrapper = success.previousAddress.get
        previousAddressWrapper.movedRecently should be(Some(false))
      }
    )
  }

  // TST5
  it should "error out with movedRecently=false, valid postcode and click on 'Find address'" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "false",
        "previousAddress.findAddress" -> "true",
        "previousAddress.previousAddress.postcode" -> "WR26NJ"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("previousAddress.movedRecently") should be(Seq("Please click 'Continue' if you haven't moved"))
        hasErrors.globalErrorMessages should be(Seq("Please click 'Continue' if you haven't moved"))
      },
      success => fail("Should have thrown an error")
    )
  }

  // TST6
  it should "successfully bind to movedRecently=false, valid postcode and click on 'Continue'" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "false",
        "previousAddress.findAddress" -> "false",
        "previousAddress.previousAddress.postcode" -> "WR26NJ"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.previousAddress.isDefined should be(true)
        val previousAddressWrapper = success.previousAddress.get
        previousAddressWrapper.movedRecently should be(Some(false))
      }
    )
  }

  // TST7
  it should "error out with movedRecently=false, invalid postcode and click on 'Find address'" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "false",
        "previousAddress.findAddress" -> "true",
        "previousAddress.previousAddress.postcode" -> "111111"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("previousAddress.movedRecently") should be(Seq("Please click 'Continue' if you haven't moved"))
        hasErrors.globalErrorMessages should be(Seq("Please click 'Continue' if you haven't moved"))
      },
      success => fail("Should have thrown an error")
    )
  }

  // TST8
  it should "successfully bind to movedRecently=false, invalid postcode and click on 'Continue'" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "false",
        "previousAddress.findAddress" -> "false",
        "previousAddress.previousAddress.postcode" -> "111111"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.previousAddress.isDefined should be(true)
        val previousAddressWrapper = success.previousAddress.get
        previousAddressWrapper.movedRecently should be(Some(false))
      }
    )
  }

  // TST9
  it should "error out with movedRecently=true, empty postcode and click on 'Find address'" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.findAddress" -> "true"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("previousAddress.previousAddress.postcode") should be(Seq("Please enter the postcode and click 'Find address'"))
        hasErrors.globalErrorMessages should be(Seq("Please enter the postcode and click 'Find address'"))
      },
      success => fail("Should have thrown an error")
    )
  }

  // TST10
  it should "error out with movedRecently=true, empty postcode and click on 'Continue'" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.findAddress" -> "false"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("previousAddress.previousAddress.postcode") should be(Seq("Please enter the postcode and click 'Find address'"))
        hasErrors.globalErrorMessages should be(Seq("Please enter the postcode and click 'Find address'"))
      },
      success => fail("Should have thrown an error")
    )
  }

  // TST11
  it should "successfully bind to movedRecently=true, valid postcode and click on 'Find address'" in {

    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.findAddress" -> "true",
        "previousAddress.previousAddress.postcode" -> "AB12 3CD"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.previousAddress.isDefined should be(true)
        val previousAddressWrapper = success.previousAddress.get
        previousAddressWrapper.movedRecently should be(Some(true))
      }
    )
  }

  // TST12
  it should "error out with movedRecently=true, valid postcode and click on 'Continue'" in {

    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.findAddress" -> "false",
        "previousAddress.previousAddress.postcode" -> "AB12 3CD"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("previousAddress.previousAddress.postcode") should be(Seq("Please enter the postcode and click 'Find address'"))
        hasErrors.globalErrorMessages should be(Seq("Please enter the postcode and click 'Find address'"))
      },
      success => fail("Should have thrown an error")
    )
  }

  // TST13
  it should "error out with movedRecently=true, invalid postcode and click on 'Find address'" in {

    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.findAddress" -> "true",
        "previousAddress.previousAddress.postcode" -> "11111"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("previousAddress.previousAddress.postcode") should be(Seq("This postcode is not valid"))
        hasErrors.globalErrorMessages should be(Seq("This postcode is not valid"))
      },
      success => fail("Should have thrown an error")
    )
  }

  // TST14
  it should "error out with movedRecently=true, invalid postcode and click on 'Continue'" in {

    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.findAddress" -> "false",
        "previousAddress.previousAddress.postcode" -> "11111"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("previousAddress.previousAddress.postcode") should be(Seq("Please enter the postcode and click 'Find address'"))
        hasErrors.globalErrorMessages should be(Seq("Please enter the postcode and click 'Find address'"))
      },
      success => fail("Should have thrown an error")
    )
  }

  // TST15
  it should "successfully bind to movedRecently=true, valid postcode and address and click on 'Find address'" in {
    val possibleAddress = PartialAddress(addressLine = Some("123 Fake Street"),
      uprn = Some("12345678"),
      postcode = "AB12 3CD",
      manualAddress = None)
    val possibleAddressJS = serialiser.toJson(Addresses(List(possibleAddress)))
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.findAddress" -> "true",
        "previousAddress.previousAddress.uprn" -> "12345678",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.previousAddress.isDefined should be(true)
        val previousAddressWrapper = success.previousAddress.get
        previousAddressWrapper.movedRecently should be(Some(true))

        previousAddressWrapper.previousAddress.isDefined should be(true)
        val previousAddress = previousAddressWrapper.previousAddress.get
        previousAddress.uprn should be(Some("12345678"))
        previousAddress.postcode should be("SW1A 1AA")

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses
        possibleAddresses.jsonList.addresses should be(List(possibleAddress))
      }
    )
  }

  // TST16
  it should "successfully bind to movedRecently=true, valid postcode and address and click on 'Continue'" in {
    val possibleAddress = PartialAddress(addressLine = Some("123 Fake Street"),
      uprn = Some("12345678"),
      postcode = "AB12 3CD",
      manualAddress = None)
    val possibleAddressJS = serialiser.toJson(Addresses(List(possibleAddress)))
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.findAddress" -> "false",
        "previousAddress.previousAddress.uprn" -> "12345678",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.previousAddress.isDefined should be(true)
        val previousAddressWrapper = success.previousAddress.get
        previousAddressWrapper.movedRecently should be(Some(true))

        previousAddressWrapper.previousAddress.isDefined should be(true)
        val previousAddress = previousAddressWrapper.previousAddress.get
        previousAddress.uprn should be(Some("12345678"))
        previousAddress.postcode should be("SW1A 1AA")

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses
        possibleAddresses.jsonList.addresses should be(List(possibleAddress))
      }
    )
  }

  // TST17
  it should "successfully bind to movedRecently=true, valid postcode and manual address and click on 'Find address'" in {
    val possibleAddress = PartialAddress(addressLine = Some("123 Fake Street"),
      uprn = Some("12345678"),
      postcode = "AB12 3CD",
      manualAddress = None)
    val possibleAddressJS = serialiser.toJson(Addresses(List(possibleAddress)))
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.findAddress" -> "true",
        "previousAddress.previousAddress.manualAddress" -> "123 Fake Street",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.previousAddress.isDefined should be(true)
        val previousAddressWrapper = success.previousAddress.get
        previousAddressWrapper.movedRecently should be(Some(true))

        previousAddressWrapper.previousAddress.isDefined should be(true)
        val previousAddress = previousAddressWrapper.previousAddress.get
        previousAddress.uprn should be(None)
        previousAddress.manualAddress should be(Some("123 Fake Street"))
        previousAddress.postcode should be("SW1A 1AA")

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses
        possibleAddresses.jsonList.addresses should be(List(possibleAddress))
      }
    )
  }

  // TST18
  it should "successfully bind to movedRecently=true, valid postcode and manual address and click on 'Continue'" in {
    val possibleAddress = PartialAddress(addressLine = Some("123 Fake Street"),
      uprn = Some("12345678"),
      postcode = "AB12 3CD",
      manualAddress = None)
    val possibleAddressJS = serialiser.toJson(Addresses(List(possibleAddress)))
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.findAddress" -> "false",
        "previousAddress.previousAddress.manualAddress" -> "123 Fake Street",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.previousAddress.isDefined should be(true)
        val previousAddressWrapper = success.previousAddress.get
        previousAddressWrapper.movedRecently should be(Some(true))

        previousAddressWrapper.previousAddress.isDefined should be(true)
        val previousAddress = previousAddressWrapper.previousAddress.get
        previousAddress.uprn should be(None)
        previousAddress.manualAddress should be(Some("123 Fake Street"))
        previousAddress.postcode should be("SW1A 1AA")

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses
        possibleAddresses.jsonList.addresses should be(List(possibleAddress))
      }
    )
  }

  // TST19
  it should "successfully bind to movedRecently=true, valid postcode and empty address (neither list selection or manual input) > click on 'Find address'" in {
    val possibleAddress = PartialAddress(addressLine = Some("123 Fake Street"),
      uprn = Some("12345678"),
      postcode = "AB12 3CD",
      manualAddress = None)
    val possibleAddressJS = serialiser.toJson(Addresses(List(possibleAddress)))
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.findAddress" -> "true",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.previousAddress.isDefined should be(true)
        val previousAddressWrapper = success.previousAddress.get
        previousAddressWrapper.movedRecently should be(Some(true))

        previousAddressWrapper.previousAddress.isDefined should be(true)
        val previousAddress = previousAddressWrapper.previousAddress.get
        previousAddress.uprn should be(None)
        previousAddress.manualAddress should be(None)
        previousAddress.postcode should be("SW1A 1AA")

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses
        possibleAddresses.jsonList.addresses should be(List(possibleAddress))
      }
    )
  }

  // TST20
  it should "successfully bind to movedRecently=true, valid postcode and empty address (neither list selection or manual input) > click on 'Continue'" in {
    val possibleAddress = PartialAddress(addressLine = Some("123 Fake Street"),
      uprn = Some("12345678"),
      postcode = "AB12 3CD",
      manualAddress = None)
    val possibleAddressJS = serialiser.toJson(Addresses(List(possibleAddress)))
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.findAddress" -> "false",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("previousAddress.previousAddress.uprn") should be(Seq("Please select your address"))
        hasErrors.globalErrorMessages should be(Seq("Please select your address"))
      },
      success => fail("Should have thrown an error")
    )
  }




  it should "successfully bind to address and movedRecently=true" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.previousAddress.uprn" -> "12345678",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.previousAddress.isDefined should be(true)
        val previousAddressWrapper = success.previousAddress.get
        previousAddressWrapper.movedRecently should be(Some(true))
        
        previousAddressWrapper.previousAddress.isDefined should be(true)
        val previousAddress = previousAddressWrapper.previousAddress.get
        previousAddress.uprn should be(Some("12345678"))
        previousAddress.postcode should be("SW1A 1AA")
      }
    )
  }

  it should "successfully bind to address and movedRecently=true (manual address)" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.previousAddress.manualAddress" -> "123 Fake Street",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.previousAddress.isDefined should be(true)
        val previousAddressWrapper = success.previousAddress.get
        previousAddressWrapper.movedRecently should be(Some(true))

        previousAddressWrapper.previousAddress.isDefined should be(true)
        val previousAddress = previousAddressWrapper.previousAddress.get
        previousAddress.manualAddress should be(Some("123 Fake Street"))
        previousAddress.postcode should be("SW1A 1AA")
      }
    )
  }

  it should "successfully bind to address and movedRecently=true with possible addresses" in {
    val possibleAddress = PartialAddress(addressLine = Some("123 Fake Street"), 
                                         uprn = Some("12345678"),
                                         postcode = "AB12 3CD", 
                                         manualAddress = None)
    val possibleAddressJS = serialiser.toJson(Addresses(List(possibleAddress)))
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.previousAddress.uprn" -> "12345678",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.previousAddress.isDefined should be(true)
        val previousAddressWrapper = success.previousAddress.get
        previousAddressWrapper.movedRecently should be(Some(true))

        previousAddressWrapper.previousAddress.isDefined should be(true)
        val previousAddress = previousAddressWrapper.previousAddress.get
        previousAddress.uprn should be(Some("12345678"))
        previousAddress.postcode should be("SW1A 1AA")

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses
        possibleAddresses.jsonList.addresses should be(List(possibleAddress))
      }
    )
  }

  it should "successfully bind to address and movedRecently=true with possible addresses (manual address)" in {
    val possibleAddress = PartialAddress(addressLine = Some("123 Fake Street"), 
                                         uprn = Some("12345678"),
                                         postcode = "AB12 3CD", 
                                         manualAddress = None)
    val possibleAddressJS = serialiser.toJson(Addresses(List(possibleAddress)))
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.previousAddress.manualAddress" -> "123 Fake Street",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.previousAddress.isDefined should be(true)
        val previousAddressWrapper = success.previousAddress.get
        previousAddressWrapper.movedRecently should be(Some(true))

        previousAddressWrapper.previousAddress.isDefined should be(true)
        val previousAddress = previousAddressWrapper.previousAddress.get
        previousAddress.manualAddress should be(Some("123 Fake Street"))
        previousAddress.postcode should be("SW1A 1AA")

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses
        possibleAddresses.jsonList.addresses should be(List(possibleAddress))
      }
    )
  }

  it should "not error out with empty text" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.previousAddress.uprn" -> "12345678",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> "",
        "possibleAddresses.postcode" -> ""
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.previousAddress.isDefined should be(true)
        val previousAddressWrapper = success.previousAddress.get
        previousAddressWrapper.movedRecently should be(Some(true))

        previousAddressWrapper.previousAddress.isDefined should be(true)
        val previousAddress = previousAddressWrapper.previousAddress.get
        previousAddress.uprn should be(Some("12345678"))
        previousAddress.postcode should be("SW1A 1AA")

        success.possibleAddresses should be(None)
      }
    )
  }

  it should "not error out with empty text (manual address)" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.previousAddress.manualAddress" -> "123 Fake Street",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> "",
        "possibleAddresses.postcode" -> ""
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.previousAddress.isDefined should be(true)
        val previousAddressWrapper = success.previousAddress.get
        previousAddressWrapper.movedRecently should be(Some(true))

        previousAddressWrapper.previousAddress.isDefined should be(true)
        val previousAddress = previousAddressWrapper.previousAddress.get
        previousAddress.manualAddress should be(Some("123 Fake Street"))
        previousAddress.postcode should be("SW1A 1AA")

        success.possibleAddresses should be(None)
      }
    )
  }

  it should "successfully bind to no address and movedRecently=false" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "false"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.previousAddress.isDefined should be(true)
        val previousAddressWrapper = success.previousAddress.get
        previousAddressWrapper.movedRecently should be(Some(false))

        previousAddressWrapper.previousAddress should be(None)
      }
    )
  }

  it should "error out with no address and movedRecently=true" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("previousAddress.previousAddress.postcode") should be(Seq("Please enter the postcode and click 'Find address'"))
        hasErrors.globalErrorMessages should be(Seq("Please enter the postcode and click 'Find address'"))
      },
      success => fail("Should have thrown an error")
    )
  }


}
