package uk.gov.gds.ier.model

import uk.gov.gds.ier.model._
import uk.gov.gds.ier.test.TestHelpers
import org.scalatest.{Matchers, FlatSpec}

class OverseasApplicationTests
  extends FlatSpec
  with Matchers
  with TestHelpers {

  it should "generate expected new voter payload" in {
    val application = OverseasApplication(
      name = Some(Name(
        firstName = "John",
        middleNames = Some("James"),
        lastName = "Smith"
      )),
      previousName = Some(PreviousName(
        hasPreviousName = true,
        previousName = Some(Name(
          firstName = "James",
          middleNames = Some("John"),
          lastName = "Smith"
        ))
      )),
      previouslyRegistered = Some(PreviouslyRegistered(
        hasPreviouslyRegistered = false
      )),
      dateLeftUk = Some(DateLeft(
        month = 1,
        year = 1990
      )),
      dateLeftSpecial = None,
      lastRegisteredToVote = Some(LastRegisteredToVote(
        lastRegisteredType = LastRegisteredType.Ordinary
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
        addressDetails = Some("123 Rue de Fake, Saint-Fake")
      )),
      lastUkAddress = Some(Address(
        lineOne = Some("The (fake) Manor House"),
        lineTwo = Some("123 Fake Street"),
        lineThree = Some("North Fake"),
        city = Some("Fakerton"),
        county = Some("Fakesbury"),
        postcode = "XX12 34XX",
        uprn = Some("12345")
      )),
      openRegisterOptin = Some(true),
      waysToVote = Some(WaysToVote(
        waysToVoteType = WaysToVoteType.ByPost
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
      )),
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
      ))
    )

    val apiMap = application.toApiMap

    apiMap.get("fn") should be(Some("John"))
    apiMap.get("mn") should be(Some("James"))
    apiMap.get("ln") should be(Some("Smith"))

    apiMap.get("applicationType") should be(Some("overseas"))
    
    apiMap.get("pfn") should be(Some("James"))
    apiMap.get("pmn") should be(Some("John"))
    apiMap.get("pln") should be(Some("Smith"))
    
    apiMap.get("dob") should be(Some("1980-12-01"))

    apiMap.get("nino") should be(Some("XX 12 34 56 D"))
    apiMap.get("nonino") should be(None)

    apiMap.get("regproperty") should be(Some("The (fake) Manor House"))
    apiMap.get("regstreet") should be(Some("123 Fake Street"))
    apiMap.get("reglocality") should be(Some("North Fake"))
    apiMap.get("regtown") should be(Some("Fakerton"))
    apiMap.get("regarea") should be(Some("Fakesbury"))
    apiMap.get("reguprn") should be(Some("12345"))
    apiMap.get("regpostcode") should be(Some("XX12 34XX"))
    
    apiMap.get("corraddress") should be(Some("123 Rue de Fake, Saint-Fake"))
    apiMap.get("corrcountry") should be(Some("France"))

    apiMap.get("lastcategory") should be(Some("ordinary"))
    
    apiMap.get("leftuk") should be(Some("1990-01"))
    apiMap.get("dcs") should be(None)

    apiMap.get("pgfn") should be(None)
    apiMap.get("pgmn") should be(None)
    apiMap.get("pgln") should be(None)
    apiMap.get("pgnc") should be(None)
    apiMap.get("pgrfn") should be(None)
    apiMap.get("pgrmn") should be(None)
    apiMap.get("pgrln") should be(None)

    apiMap.get("bpass") should be(Some("true"))
    apiMap.get("passno") should be(Some("123456789"))
    apiMap.get("passloc") should be(Some("Uk border office"))
    apiMap.get("passdate") should be(Some("2000-12-01"))
    apiMap.get("dbritcit") should be(None)
    apiMap.get("hbritcit") should be(None)
    apiMap.get("pvote") should be(Some("true"))
    apiMap.get("proxyvote") should be(None)

    apiMap.get("pvoteemail") should be(None)
    apiMap.get("proxyvoteemail") should be(None)
    apiMap.get("opnreg") should be(Some("true"))
    apiMap.get("email") should be(Some("test@email.com"))
    apiMap.get("phone") should be(Some("01234 5678910"))
  }

  it should "generate expected renewer payload" in {
    val application = OverseasApplication(
      name = Some(Name(
        firstName = "John",
        middleNames = Some("James"),
        lastName = "Smith"
      )),
      previousName = Some(PreviousName(
        hasPreviousName = true,
        previousName = Some(Name(
          firstName = "James",
          middleNames = Some("John"),
          lastName = "Smith"
        ))
      )),
      previouslyRegistered = Some(PreviouslyRegistered(
        hasPreviouslyRegistered = true
      )),
      dateLeftUk = Some(DateLeft(
        month = 1,
        year = 1990
      )),
      dateLeftSpecial = None,
      lastRegisteredToVote = None,
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
        addressDetails = Some("123 Rue de Fake, Saint-Fake")
      )),
      lastUkAddress = Some(Address(
        lineOne = Some("The (fake) Manor House"),
        lineTwo = Some("123 Fake Street"),
        lineThree = Some("North Fake"),
        city = Some("Fakerton"),
        county = Some("Fakesbury"),
        postcode = "XX12 34XX",
        uprn = Some("12345")
      )),
      openRegisterOptin = Some(true),
      waysToVote = Some(WaysToVote(
        waysToVoteType = WaysToVoteType.InPerson
      )),
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
      ))
    )

    val apiMap = application.toApiMap

    apiMap.get("fn") should be(Some("John"))
    apiMap.get("mn") should be(Some("James"))
    apiMap.get("ln") should be(Some("Smith"))

    apiMap.get("applicationType") should be(Some("overseas"))
    
    apiMap.get("pfn") should be(Some("James"))
    apiMap.get("pmn") should be(Some("John"))
    apiMap.get("pln") should be(Some("Smith"))
    
    apiMap.get("dob") should be(Some("1980-12-01"))

    apiMap.get("nino") should be(Some("XX 12 34 56 D"))
    apiMap.get("nonino") should be(None)

    apiMap.get("regproperty") should be(Some("The (fake) Manor House"))
    apiMap.get("regstreet") should be(Some("123 Fake Street"))
    apiMap.get("reglocality") should be(Some("North Fake"))
    apiMap.get("regtown") should be(Some("Fakerton"))
    apiMap.get("regarea") should be(Some("Fakesbury"))
    apiMap.get("reguprn") should be(Some("12345"))
    apiMap.get("regpostcode") should be(Some("XX12 34XX"))
    
    apiMap.get("corraddress") should be(Some("123 Rue de Fake, Saint-Fake"))
    apiMap.get("corrcountry") should be(Some("France"))

    apiMap.get("lastcategory") should be(Some("overseas"))
    
    apiMap.get("leftuk") should be(Some("1990-01"))
    apiMap.get("dcs") should be(None)

    apiMap.get("pgfn") should be(None)
    apiMap.get("pgmn") should be(None)
    apiMap.get("pgln") should be(None)
    apiMap.get("pgnc") should be(None)
    apiMap.get("pgrfn") should be(None)
    apiMap.get("pgrmn") should be(None)
    apiMap.get("pgrln") should be(None)

    apiMap.get("bpass") should be(None)
    apiMap.get("passno") should be(None)
    apiMap.get("passloc") should be(None)
    apiMap.get("passdate") should be(None)
    apiMap.get("dbritcit") should be(None)
    apiMap.get("hbritcit") should be(None)
    apiMap.get("pvote") should be(None)
    apiMap.get("proxyvote") should be(None)

    apiMap.get("pvoteemail") should be(None)
    apiMap.get("proxyvoteemail") should be(None)
    apiMap.get("opnreg") should be(Some("true"))
    apiMap.get("email") should be(Some("test@email.com"))
    apiMap.get("phone") should be(Some("01234 5678910"))
  }
}
