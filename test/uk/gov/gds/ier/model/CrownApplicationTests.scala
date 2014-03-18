package uk.gov.gds.ier.model

import uk.gov.gds.common.model.LocalAuthority
import uk.gov.gds.ier.test.{TestHelpers, CustomMatchers}
import org.scalatest.{Matchers, FlatSpec}

class CrownApplicationTests
  extends FlatSpec
  with Matchers
  with CustomMatchers
  with TestHelpers {

  it should "generate the expected payload" in {
    lazy val application = CrownApplication(

      statement = Some(CrownStatement(
        crownMember = Some(true),
        partnerCrownMember = None,
        britishCouncilMember = None,
        partnerBritishCouncilMember = None
      )),
      address = Some(Address(
        lineOne = Some("The (fake) Manor House"),
        lineTwo = Some("123 Fake Street"),
        lineThree = Some("North Fake"),
        city = Some("Fakerton"),
        county = Some("Fakesbury"),
        postcode = "XX12 34XX",
        uprn = Some("12345")
      )),
      nationality = Some(IsoNationality(
        countryIsos = List("GB", "IE"),
        noNationalityReason = None
      )),
      dob = Some(DateOfBirth(
        dob = Some(DOB(
          year = 1980,
          month = 12,
          day = 1
        )),
        noDob = None
      )),
      name = Some(Name(
        firstName = "John",
        middleNames = Some("James"),
        lastName = "Smith"
      )),
      previousName = Some(PreviousName(
        hasPreviousName = true,
        previousName = Some(Name(
          firstName = "George",
          middleNames = Some("Jeffrey"),
          lastName = "Smith"
        ))
      )),
      nino = Some(Nino(
        nino = Some("XX 12 34 56 D"),
        noNinoReason = None
      )),
      job = Some(Job(
        jobTitle = Some("my job title"),
        govDepartment = Some("MoJ")
      )),
      contactAddress = Some (PossibleContactAddresses(
        contactAddressType = Some("uk"),
        ukAddressLine = Some("my uk address, london"),
        bfpoContactAddress = None,
        otherContactAddress = None
      )),
      openRegisterOptin = Some(false),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByPost,
        postalVoteOption = Some(true),
        deliveryMethod = Some(PostalVoteDeliveryMethod(
          deliveryMethod = Some("post"),
          emailAddress = None
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
      authority = Some(LocalAuthority(
        name = "Camden Borough Council",
        opcsId = "00AG",
        gssId = "E09000007"
      ))
    )

    val apiMap = application.toApiMap

    val expected = Map(
      "applicationType" -> "crown",
      "refNum" -> "12345678910",
      "ip" -> "256.256.256.256",
      "fn" -> "John",
      "mn" -> "James",
      "ln" -> "Smith",
      "pfn" -> "George",
      "pmn" -> "Jeffrey",
      "pln" -> "Smith",
      "dob" -> "1980-12-01",
      "nino" -> "XX 12 34 56 D",
      "nat" -> "GB, IE",
      "regproperty" -> "The (fake) Manor House",
      "regstreet" -> "123 Fake Street",
      "reglocality" -> "North Fake",
      "regtown" -> "Fakerton",
      "regarea" -> "Fakesbury",
      "reguprn" -> "12345",
      "regpostcode" -> "XX12 34XX",
      "corrcountry" -> "uk",
      "corrpostcode" -> "XX12 34XX",
      "corraddressline1" -> "The (fake) Manor House",
      "corraddressline2" -> "123 Fake Street",
      "corraddressline3" -> "North Fake",
      "corraddressline4" -> "Fakerton",
      "corraddressline5" -> "Fakesbury",
      "scrwn" -> "false",
      "bc" -> "false",
      "sbc" -> "false",
      "role" -> "my job title",
      "dept" -> "MoJ",
      "pvote" -> "true",
      "opnreg" -> "false",
      "post" -> "true",
      "email" -> "test@email.com",
      "phone" -> "01234 5678910",
      "gssCode" -> "E09000007"
    )

    val notExpected = List(
      "nodobReason",
      "agerange",
      "nonino",
      "nonat"
    )

    apiMap should matchMap(expected)

    for(key <- notExpected) {
      apiMap.keys should not contain(key)
    }

    apiMap.keys.size should be(37)
  }
}