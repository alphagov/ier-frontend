package uk.gov.gds.ier.model

import uk.gov.gds.common.model.LocalAuthority
import uk.gov.gds.ier.test.{TestHelpers, CustomMatchers}
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.service.apiservice.ForcesApplication

class ForcesApplicationTests
  extends FlatSpec
  with Matchers
  with CustomMatchers
  with TestHelpers {

  it should "generate the expected payload" in {
    lazy val application = ForcesApplication(

      statement = Some(Statement(
        memberForcesFlag = Some(true),
        partnerForcesFlag = None
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
      previousAddress = Some(Address(
        lineOne = Some("The (fake) Cottage"),
        lineTwo = Some("321 Fake Street"),
        lineThree = Some("South Fake"),
        city = Some("Fakererly"),
        county = Some("Fakesborough"),
        postcode = "XX34 21XX",
        uprn = Some("54321")
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
      service = Some(Service(
        serviceName = Some(ServiceType.RoyalAirForce),
        regiment = None
      )),
      rank = Some(Rank(
        serviceNumber = Some("1234567"),
        rank = Some("Captain")
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
      "applicationType" -> "forces",
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
      "pproperty" -> "The (fake) Cottage",
      "pstreet" -> "321 Fake Street",
      "plocality" -> "South Fake",
      "ptown" -> "Fakererly",
      "parea" -> "Fakesborough",
      "puprn" -> "54321",
      "ppostcode" -> "XX34 21XX",
      "corrcountry" -> "uk",
      "corrpostcode" -> "XX12 34XX",
      "corraddressline1" -> "The (fake) Manor House",
      "corraddressline2" -> "123 Fake Street",
      "corraddressline3" -> "North Fake",
      "corraddressline4" -> "Fakerton",
      "corraddressline5" -> "Fakesbury",
      "saf" -> "false",
      "rank" -> "Captain",
      "serv" -> "Royal Air Force",
      "servno" -> "1234567",
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

    apiMap.keys.size should be(43)
  }
}
