package uk.gov.gds.ier.model

import uk.gov.gds.ier.test.{TestHelpers, CustomMatchers}
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.service.apiservice.OrdinaryApplication

class OrdinaryApplicationTests
  extends FlatSpec
  with Matchers
  with CustomMatchers
  with TestHelpers {

  behavior of "OrdinaryApplication.toApiMap"

  it should "generate the expected payload map - simple case" in {
    lazy val application = createOrdinaryApplication

    val expected = Map(
      "fn" -> "John",
      "mn" -> "James",
      "ln" -> "Smith",
      "applicationType" -> "ordinary",
      "pfn" -> "James",
      "pmn" -> "John",
      "pln" -> "Smith",
      "dob" -> "1980-12-01",
      "nino" -> "XX 12 34 56 D",
      "nat" -> "GB, IE",
      "oadr" -> "none",
      "regproperty" -> "The (fake) Manor House",
      "regstreet" -> "123 Fake Street",
      "reglocality" -> "North Fake",
      "regtown" -> "Fakerton",
      "regarea" -> "Fakesbury",
      "reguprn" -> "12345",
      "regpostcode" -> "xx1234xx",
      "pproperty" -> "The (fake) Cottage",
      "pstreet" -> "321 Fake Street",
      "plocality" -> "South Fake",
      "ptown" -> "Fakererly",
      "parea" -> "Fakesborough",
      "puprn" -> "54321",
      "ppostcode" -> "xx3421xx",
      "pvote" -> "true",
      "pvoteemail" -> "postal@vote.com",
      "opnreg" -> "false",
      "post" -> "true",
      "email" -> "test@email.com",
      "phone" -> "01234 5678910",
      "refNum" -> "12345678910",
      "ip" -> "256.256.256.256",
      "gssCode" -> "E09000007",
      "pgssCode" -> "E09000032",
      "timeTaken" -> "1234",
      "lang" -> "en"
    )

    val apiMap = application.toApiMap

    apiMap should matchMap(expected)
  }

  it should "generate the expected payload when registered while abroad" in {
    lazy val application = createOrdinaryApplication.copy(
      lastRegisteredToVote = Some(LastRegisteredToVote(
        lastRegisteredType = LastRegisteredType.Overseas
      ))
    )

    val expected = Map(
      "fn" -> "John",
      "mn" -> "James",
      "ln" -> "Smith",
      "applicationType" -> "ordinary",
      "pfn" -> "James",
      "pmn" -> "John",
      "pln" -> "Smith",
      "dob" -> "1980-12-01",
      "nino" -> "XX 12 34 56 D",
      "nat" -> "GB, IE",
      "oadr" -> "none",
      "regproperty" -> "The (fake) Manor House",
      "regstreet" -> "123 Fake Street",
      "reglocality" -> "North Fake",
      "regtown" -> "Fakerton",
      "regarea" -> "Fakesbury",
      "reguprn" -> "12345",
      "regpostcode" -> "xx1234xx",
      "pproperty" -> "The (fake) Cottage",
      "pstreet" -> "321 Fake Street",
      "plocality" -> "South Fake",
      "ptown" -> "Fakererly",
      "parea" -> "Fakesborough",
      "puprn" -> "54321",
      "ppostcode" -> "xx3421xx",
      "pvote" -> "true",
      "pvoteemail" -> "postal@vote.com",
      "opnreg" -> "false",
      "post" -> "true",
      "email" -> "test@email.com",
      "phone" -> "01234 5678910",
      "refNum" -> "12345678910",
      "ip" -> "256.256.256.256",
      "gssCode" -> "E09000007",
      "pgssCode" -> "E09000032",
      "lastcategory" -> "overseas",
      "timeTaken" -> "1234",
      "lang" -> "en"
    )

    val apiMap = application.toApiMap

    apiMap should matchMap(expected)
  }

  it should "generate the expected payload when application submitted in Welsh" in {
    lazy val application = createOrdinaryApplication.copy(language = "cy")

    val expected = Map(
      "fn" -> "John",
      "mn" -> "James",
      "ln" -> "Smith",
      "applicationType" -> "ordinary",
      "pfn" -> "James",
      "pmn" -> "John",
      "pln" -> "Smith",
      "dob" -> "1980-12-01",
      "nino" -> "XX 12 34 56 D",
      "nat" -> "GB, IE",
      "oadr" -> "none",
      "regproperty" -> "The (fake) Manor House",
      "regstreet" -> "123 Fake Street",
      "reglocality" -> "North Fake",
      "regtown" -> "Fakerton",
      "regarea" -> "Fakesbury",
      "reguprn" -> "12345",
      "regpostcode" -> "xx1234xx",
      "pproperty" -> "The (fake) Cottage",
      "pstreet" -> "321 Fake Street",
      "plocality" -> "South Fake",
      "ptown" -> "Fakererly",
      "parea" -> "Fakesborough",
      "puprn" -> "54321",
      "ppostcode" -> "xx3421xx",
      "pvote" -> "true",
      "pvoteemail" -> "postal@vote.com",
      "opnreg" -> "false",
      "post" -> "true",
      "email" -> "test@email.com",
      "phone" -> "01234 5678910",
      "refNum" -> "12345678910",
      "ip" -> "256.256.256.256",
      "gssCode" -> "E09000007",
      "pgssCode" -> "E09000032",
      "timeTaken" -> "1234",
      "lang" -> "cy"
    )

    val apiMap = application.toApiMap

    apiMap should matchMap(expected)
  }



  private def createOrdinaryApplication =
    OrdinaryApplication(
      name = Some(Name(
        firstName = "John",
        middleNames = Some("James"),
        lastName = "Smith")),
      previousName = Some(PreviousName(
        hasPreviousName = true,
        previousName = Some(Name(
          firstName = "James",
          middleNames = Some("John"),
          lastName = "Smith"
        ))
      )),
      lastRegisteredToVote = None,
      dob = Some(DateOfBirth(
        dob = Some(DOB(
          year = 1980,
          month = 12,
          day = 1
        )),
        noDob = None
      )),
      nationality = Some(IsoNationality(
        countryIsos = List("GB", "IE"),
        noNationalityReason = None
      )),
      nino = Some(Nino(
        nino = Some("XX 12 34 56 D"),
        noNinoReason = None
      )),
      address = Some(Address(
        lineOne = Some("The (fake) Manor House"),
        lineTwo = Some("123 Fake Street"),
        lineThree = Some("North Fake"),
        city = Some("Fakerton"),
        county = Some("Fakesbury"),
        postcode = "XX12 34XX",
        uprn = Some("12345"),
        gssCode = Some("E09000007")
      )),
      previousAddress = Some(Address(
        lineOne = Some("The (fake) Cottage"),
        lineTwo = Some("321 Fake Street"),
        lineThree = Some("South Fake"),
        city = Some("Fakererly"),
        county = Some("Fakesborough"),
        postcode = "XX34 21XX",
        uprn = Some("54321"),
        gssCode = Some("E09000032")
      )),
      otherAddress = Some(OtherAddress(
        otherAddressOption = OtherAddress.NoOtherAddress
      )),
      openRegisterOptin = Some(false),
      postalVote = Some(PostalVote(
        postalVoteOption = Some(true),
        deliveryMethod = Some(PostalVoteDeliveryMethod(
          deliveryMethod = Some("email"),
          emailAddress = Some("postal@vote.com")
        ))
      )),
      contact = Some(Contact(
        post = true,
        email = Some(ContactDetail(
          contactMe = true,
          detail = Some("test@email.com")
        )),
        phone = Some(ContactDetail(
          contactMe = true,
          detail = Some("01234 5678910")
        ))
      )),
      referenceNumber = Some("12345678910"),
      ip = Some("256.256.256.256"),
      timeTaken = "1234",
      language = "en"
    )
}
