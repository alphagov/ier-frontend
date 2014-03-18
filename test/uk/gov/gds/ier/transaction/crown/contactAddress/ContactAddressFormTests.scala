package uk.gov.gds.ier.transaction.crown.contactAddress

import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.Json
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{ContactAddress, PossibleContactAddresses}

class ContactAddressFormTests
  extends FlatSpec
  with Matchers
  with ContactAddressForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  it should "error out on empty input" in {
    val emptyRequest = Map.empty[String, String]
    contactAddressForm.bind(emptyRequest).fold(
      formWithErrors => {
        formWithErrors.errorMessages("contactAddress.contactAddressType") should be(
          Seq("Please answer this question"))
        formWithErrors.globalErrorMessages should be (Seq("Please answer this question"))
        formWithErrors.errors.size should be(2)

      },
      formWithSuccess => fail("Should have thrown an error")
    )
  }

  it should "bind successfully on uk address" in {
    val request = Json.toJson(
      Map(
        "contactAddress.contactAddressType" -> "uk"
      )
    )
    contactAddressForm.bind(request).fold(
      formWithErrors => fail(serialiser.toJson(formWithErrors.prettyPrint)),
      formWithSuccess => {
        formWithSuccess.contactAddress.isDefined should be(true)
        formWithSuccess.contactAddress should be(
          Some(PossibleContactAddresses(Some("uk"),None,None,None)))
      }
    )
  }

  it should "bind successfully on bfpo address" in {
    val request = Json.toJson(
      Map(
        "contactAddress.contactAddressType" -> "bfpo",
        "contactAddress.bfpoContactAddress.addressLine1" -> "address line 1",
        "contactAddress.bfpoContactAddress.addressLine2" -> "address line 2, 456 - 457",
        "contactAddress.bfpoContactAddress.addressLine3" -> "London",
        "contactAddress.bfpoContactAddress.postcode" -> "BFPO90-987 XXZ"
      )
    )
    contactAddressForm.bind(request).fold(
      formWithErrors => fail(serialiser.toJson(formWithErrors.prettyPrint)),
      formWithSuccess => {
        formWithSuccess.contactAddress.isDefined should be(true)
        formWithSuccess.contactAddress should be(
          Some(PossibleContactAddresses(
            contactAddressType = Some("bfpo"),
            ukAddressLine = None,
            bfpoContactAddress = Some(ContactAddress(
              country = None,
              postcode = Some("BFPO90-987 XXZ"),
              addressLine1 = Some("address line 1"),
              addressLine2 = Some("address line 2, 456 - 457"),
              addressLine3 = Some("London"),
              addressLine4 = None,
              addressLine5 = None
            )),
            otherContactAddress = None
          ))
        )
      }
    )
  }

  it should "bind successfully on other address" in {
    val request = Json.toJson(
      Map(
        "contactAddress.contactAddressType" -> "other",
        "contactAddress.otherContactAddress.addressLine1" -> "Francisco de quevedo 54",
        "contactAddress.otherContactAddress.addressLine2" -> "Rubí",
        "contactAddress.otherContactAddress.postcode" -> "08191",
        "contactAddress.otherContactAddress.country" -> "Spain"
      )
    )
    contactAddressForm.bind(request).fold(
      formWithErrors => fail(serialiser.toJson(formWithErrors.prettyPrint)),
      formWithSuccess => {
        formWithSuccess.contactAddress.isDefined should be(true)
        formWithSuccess.contactAddress should be(
          Some(PossibleContactAddresses(
            contactAddressType = Some("other"),
            ukAddressLine = None,
            bfpoContactAddress = None,
            otherContactAddress = Some(ContactAddress(
              country = Some("Spain"),
              postcode = Some("08191"),
              addressLine1 = Some("Francisco de quevedo 54"),
              addressLine2 = Some("Rubí"),
              addressLine3 = None,
              addressLine4 = None,
              addressLine5 = None
            ))
          ))
        )
      }
    )
  }

}
