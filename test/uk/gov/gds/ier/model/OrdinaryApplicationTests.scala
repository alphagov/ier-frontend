package uk.gov.gds.ier.model

import uk.gov.gds.common.model.LocalAuthority
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.test.TestHelpers
import org.scalatest.{Matchers, FlatSpec}

class OrdinaryApplicationTests
  extends FlatSpec
  with Matchers
  with TestHelpers {

  it should "generate the expected payload" in {
    lazy val application = OrdinaryApplication(
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
      authority = Some(LocalAuthority(
        name = "Camden Borough Council",
        opcsId = "00AG",
        gssId = "E09000007"
      )),
      previousAuthority = Some(LocalAuthority(
        name = "Wandsworth Borough Council",
        opcsId = "00BJ",
        gssId = "E09000032"
      ))
    )

    val apiMap = application.toApiMap


    apiMap.get("fn") should be(Some("John"))
    apiMap.get("mn") should be(Some("James"))
    apiMap.get("ln") should be(Some("Smith"))

    apiMap.get("applicationType") should be(Some("ordinary"))

    apiMap.get("pfn") should be(Some("James"))
    apiMap.get("pmn") should be(Some("John"))
    apiMap.get("pln") should be(Some("Smith"))
    
    apiMap.get("dob") should be(Some("1980-12-01"))
    apiMap.get("nodobReason") should be(None)
    apiMap.get("agerange") should be(None)

    apiMap.get("nino") should be(Some("XX 12 34 56 D"))
    apiMap.get("nonino") should be(None)

    apiMap.get("nat") should be(Some("GB, IE"))
    apiMap.get("nonat") should be(None)

    apiMap.get("oadr") should be(Some("false"))

    apiMap.get("regproperty") should be(Some("The (fake) Manor House"))
    apiMap.get("regstreet") should be(Some("123 Fake Street"))
    apiMap.get("reglocality") should be(Some("North Fake"))
    apiMap.get("regtown") should be(Some("Fakerton"))
    apiMap.get("regarea") should be(Some("Fakesbury"))
    apiMap.get("reguprn") should be(Some("12345"))
    apiMap.get("regpostcode") should be(Some("XX12 34XX"))

    apiMap.get("pproperty") should be(Some("The (fake) Cottage"))
    apiMap.get("pstreet") should be(Some("321 Fake Street"))
    apiMap.get("plocality") should be(Some("South Fake"))
    apiMap.get("ptown") should be(Some("Fakererly"))
    apiMap.get("parea") should be(Some("Fakesborough"))
    apiMap.get("puprn") should be(Some("54321"))
    apiMap.get("ppostcode") should be(Some("XX34 21XX"))

    apiMap.get("pvote") should be(Some("true"))
    apiMap.get("pvoteemail") should be(Some("postal@vote.com"))

    apiMap.get("opnreg") should be(Some("false"))

    apiMap.get("email") should be(Some("test@email.com"))
    apiMap.get("phone") should be(Some("01234 5678910"))

    apiMap.get("refNum") should be(Some("12345678910"))
    apiMap.get("ip") should be(Some("256.256.256.256"))
    apiMap.get("gssCode") should be(Some("E09000007"))
    apiMap.get("pgssCode") should be(Some("E09000032"))
  }
}
