package uk.gov.gds.ier.model

import uk.gov.gds.ier.test.{TestHelpers, CustomMatchers}
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.service.apiservice.OverseasApplication

class OverseasApplicationTests
  extends FlatSpec
  with Matchers
  with TestHelpers
  with CustomMatchers {

  behavior of "OverseasApplication.toApiMap"

  it should "generate expected new voter payload" in {
    val application = createOverseasApplication.copy(
      lastRegisteredToVote = Some(LastRegisteredToVote(
        lastRegisteredType = LastRegisteredType.Ordinary
      )),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByPost,
        postalVoteOption = Some(true),
        deliveryMethod = Some(PostalVoteDeliveryMethod(
          deliveryMethod = Some("post"),
          emailAddress = None
        ))
      )),
      passport = Some(Passport(
        hasPassport = true,
        bornInsideUk = None,
        details = Some(PassportDetails(
          passportNumber = "123456789",
          authority = "Uk border office",
          issueDate = DOB(
            day = 1,
            month = 12,
            year = 2000
          )
        )),
        citizen = None
      ))
    )

    val expected = Map(
      "fn" -> "John",
      "mn" -> "James",
      "ln" -> "Smith",
      "applicationType" -> "overseas",
      "pfn" -> "James",
      "pmn" -> "John",
      "pln" -> "Smith",
      "dob" -> "1980-12-01",
      "nino" -> "XX 12 34 56 D",
      "regproperty" -> "The (fake) Manor House",
      "regstreet" -> "123 Fake Street",
      "reglocality" -> "North Fake",
      "regtown" -> "Fakerton",
      "regarea" -> "Fakesbury",
      "reguprn" -> "12345",
      "regpostcode" -> "XX12 34XX",
      "corraddressline1" -> "123 Rue de Fake, Saint-Fake",
      "corrcountry" -> "France",
      "lastcategory" -> "ordinary",
      "leftuk" -> "1990-01",
      "bpass" -> "true",
      "passno" -> "123456789",
      "passloc" -> "Uk border office",
      "passdate" -> "2000-12-01",
      "pvote" -> "true",
      "opnreg" -> "true",
      "post" -> "true",
      "email" -> "test@email.com",
      "phone" -> "01234 5678910",
      "refNum" -> "12345678910",
      "ip" -> "256.256.256.256",
      "gssCode" -> "E09000007"
    )

    val apiMap = application.toApiMap

    apiMap should matchMap(expected)
  }

  it should "generate expected renewer payload" in {
    val application = createOverseasApplication.copy(
      lastRegisteredToVote = Some(LastRegisteredToVote(LastRegisteredType.Overseas))
    )

    val expected = Map(
      "fn" -> "John",
      "mn" -> "James",
      "ln" -> "Smith",
      "applicationType" -> "overseas",
      "pfn" -> "James",
      "pmn" -> "John",
      "pln" -> "Smith",
      "dob" -> "1980-12-01",
      "nino" -> "XX 12 34 56 D",
      "regproperty" -> "The (fake) Manor House",
      "regstreet" -> "123 Fake Street",
      "reglocality" -> "North Fake",
      "regtown" -> "Fakerton",
      "regarea" -> "Fakesbury",
      "reguprn" -> "12345",
      "regpostcode" -> "XX12 34XX",
      "corraddressline1" -> "123 Rue de Fake, Saint-Fake",
      "corrcountry" -> "France",
      "lastcategory" -> "overseas",
      "leftuk" -> "1990-01",
      "opnreg" -> "true",
      "post" -> "true",
      "email" -> "test@email.com",
      "phone" -> "01234 5678910",
      "refNum" -> "12345678910",
      "ip" -> "256.256.256.256",
      "gssCode" -> "E09000007"
    )

    val apiMap = application.toApiMap

    apiMap should matchMap(expected)
  }


  it should "generate expected british citizenship load" in {
    val application = createOverseasApplication.copy(
      passport = Some(Passport(
        hasPassport = false,
        bornInsideUk = None,
        details = None,
        citizen = Some(CitizenDetails(
          dateBecameCitizen = DOB(2000,6,1),
          howBecameCitizen = "by accident"
        ))
      ))
    )

    val expected = Map(
      "fn" -> "John",
      "mn" -> "James",
      "ln" -> "Smith",
      "applicationType" -> "overseas",
      "pfn" -> "James",
      "pmn" -> "John",
      "pln" -> "Smith",
      "dob" -> "1980-12-01",
      "nino" -> "XX 12 34 56 D",
      "regproperty" -> "The (fake) Manor House",
      "regstreet" -> "123 Fake Street",
      "reglocality" -> "North Fake",
      "regtown" -> "Fakerton",
      "regarea" -> "Fakesbury",
      "reguprn" -> "12345",
      "regpostcode" -> "XX12 34XX",
      "corraddressline1" -> "123 Rue de Fake, Saint-Fake",
      "corrcountry" -> "France",
      "lastcategory" -> "overseas",
      "leftuk" -> "1990-01",
      "opnreg" -> "true",
      "post" -> "true",
      "email" -> "test@email.com",
      "phone" -> "01234 5678910",
      "refNum" -> "12345678910",
      "ip" -> "256.256.256.256",
      "gssCode" -> "E09000007",
      "dbritcit" -> "2000-06-01",
      "hbritcit" -> "by accident",
      "bpass" -> "false"
    )


    val apiMap = application.toApiMap

    apiMap should matchMap(expected)
  }

  def createOverseasApplication =
    OverseasApplication(
      overseasName = Some(OverseasName(
        Some(Name(
          firstName = "John",
          middleNames = Some("James"),
          lastName = "Smith"
        )),
        Some(PreviousName(
          hasPreviousName = true,
          previousName = Some(Name(
            firstName = "James",
            middleNames = Some("John"),
            lastName = "Smith"
          ))
        ))
      )),
      dateLeftUk = Some(DateLeft(
        month = 1,
        year = 1990
      )),
      dateLeftSpecial = None,
      lastRegisteredToVote = Some(LastRegisteredToVote(
        lastRegisteredType = LastRegisteredType.Overseas
      )),
      dob = Some(DOB(
        day = 1,
        month = 12,
        year = 1980
      )),
      nino = Some(Nino(
        nino = Some("XX 12 34 56 D"),
        noNinoReason = None
      )),
      address = Some(OverseasAddress(
        country = Some("France"),
        addressLine1 = Some("123 Rue de Fake, Saint-Fake"),
        addressLine2 = None,
        addressLine3 = None,
        addressLine4 = None,
        addressLine5 = None
      )),
      lastUkAddress = Some(Address(
        lineOne = Some("The (fake) Manor House"),
        lineTwo = Some("123 Fake Street"),
        lineThree = Some("North Fake"),
        city = Some("Fakerton"),
        county = Some("Fakesbury"),
        postcode = "XX12 34XX",
        uprn = Some("12345"),
        gssCode = Some("E09000007")
      )),
      openRegisterOptin = Some(true),
      postalOrProxyVote = None,
      passport = None,
      contact = Some(Contact(
        post = true,
        phone = Some(ContactDetail(
          contactMe = true,
          detail = Some("01234 5678910")
        )),
        email = Some(ContactDetail(
          contactMe = true,
          detail = Some("test@email.com")
        ))
      )),
      referenceNumber = Some("12345678910"),
      ip = Some("256.256.256.256")
    )

}
