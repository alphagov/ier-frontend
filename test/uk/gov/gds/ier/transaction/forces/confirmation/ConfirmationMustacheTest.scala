package uk.gov.gds.ier.transaction.forces.confirmation

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.InprogressForces
import uk.gov.gds.ier.model.Name
import uk.gov.gds.ier.validation.ErrorTransformForm
import scala.Some
import uk.gov.gds.ier.model.WaysToVote

class ConfirmationMustacheTest
  extends FlatSpec
  with Matchers
  with ConfirmationForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers
  with ConfirmationMustache {

  val serialiser = jsonSerialiser

  "In-progress application form without a forces partner (member = true)" should
    "generate confirmation mustache model without forces partner block" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      statement = Some(Statement(
        memberForcesFlag = Some(true),
        partnerForcesFlag = None
      ))
    ))

    val displayPartnerBlock = Confirmation.displayPartnerBlock(
      form = partiallyFilledApplicationForm
    )

    displayPartnerBlock should be (false)
  }

  "In-progress application form without a forces partner (member and partner = true)" should
    "generate confirmation mustache model without forces partner block" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      statement = Some(Statement(
        memberForcesFlag = Some(true),
        partnerForcesFlag = Some(true)
      ))
    ))

    val displayPartnerBlock = Confirmation.displayPartnerBlock(
      form = partiallyFilledApplicationForm
    )

    displayPartnerBlock should be (false)
  }

  "In-progress application form with a forces partner" should
    "generate confirmation mustache model with forces partner block" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      statement = Some(Statement(
        memberForcesFlag = None,
        partnerForcesFlag = Some(true)
      ))
    ))

    val displayPartnerBlock = Confirmation.displayPartnerBlock(
      form = partiallyFilledApplicationForm
    )

    displayPartnerBlock should be (true)
  }


  "In-progress application form with filled name and previous name" should
    "generate confirmation mustache model with correctly rendered names and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
        name = Some(Name(
          firstName = "John",
          middleNames = None,
          lastName = "Smith"
        )),
        previousName = Some(PreviousName(
          hasPreviousName = true,
          previousName = Some(Name(
            firstName = "Jan",
            middleNames = None,
            lastName = "Kovar"
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(nameModel) = confirmation.name
    nameModel.content should be("<p>John Smith</p>")
    nameModel.editLink should be("/register-to-vote/forces/edit/name")

    val Some(prevNameModel) = confirmation.previousName
    prevNameModel.content should be("<p>Jan Kovar</p>")
    prevNameModel.editLink should be("/register-to-vote/forces/edit/name")
  }

  "In-progress application form with filled name and previous name with middle names" should
    "generate confirmation mustache model with correctly rendered names and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
        name = Some(Name(
          firstName = "John",
          middleNames = Some("Walker Junior"),
          lastName = "Smith"
        )),
        previousName = Some(PreviousName(
          hasPreviousName = true,
          previousName = Some(Name(
            firstName = "Jan",
            middleNames = Some("Janko Janik"),
            lastName = "Kovar"
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(nameModel) = confirmation.name
    nameModel.content should be("<p>John Walker Junior Smith</p>")
    nameModel.editLink should be("/register-to-vote/forces/edit/name")

    val Some(prevNameModel) = confirmation.previousName
    prevNameModel.content should be("<p>Jan Janko Janik Kovar</p>")
    prevNameModel.editLink should be("/register-to-vote/forces/edit/name")
  }

  "In-progress application form with filled date of birth" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      dob = Some(DateOfBirth(
        dob = Some(DOB(
          year = 1978,
          month = 1,
          day = 22
        )),
        noDob = None))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(dateOfBirthModel) = confirmation.dateOfBirth
    dateOfBirthModel.content should be("<p>22 January 1978</p>")
    dateOfBirthModel.editLink should be("/register-to-vote/forces/edit/date-of-birth")
  }

  "In-progress application form with filled date of birth excuse" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      dob = Some(DateOfBirth(
        dob = None,
        noDob = Some(noDOB(
          reason = Some("I have no idea!"),
          range = Some("18to70")
        ))))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(dateOfBirthModel) = confirmation.dateOfBirth
    dateOfBirthModel.content should be("<p>You are unable to provide your date of birth because: I have no idea!</p><p>I am over 18 years old</p>")
    dateOfBirthModel.editLink should be("/register-to-vote/forces/edit/date-of-birth")
  }

  "In-progress application form with british nationality" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {

    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(completeForcesApplication.copy(
      nationality = Some(PartialNationality(
        british = Some(true),
        irish = None,
        hasOtherCountry = None,
        otherCountries = List.empty,
        noNationalityReason = None
      ))
    ) )

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(nationalityModel) = confirmation.nationality
    nationalityModel.content should be("<p>I am British</p>")
    nationalityModel.editLink should be("/register-to-vote/forces/edit/nationality")
  }

  "In-progress application form with irish nationality" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(completeForcesApplication.copy(
      nationality = Some(PartialNationality(
        british = None,
        irish = Some(true),
        hasOtherCountry = None,
        otherCountries = List.empty,
        noNationalityReason = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(nationalityModel) = confirmation.nationality
    nationalityModel.content should be("<p>I am Irish</p>")
    nationalityModel.editLink should be("/register-to-vote/forces/edit/nationality")
  }

  "In-progress application form with other nationality" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(completeForcesApplication.copy(
      nationality = Some(PartialNationality(
        british = None,
        irish = None,
        hasOtherCountry = Some(true),
        otherCountries = List("Spain", "France", "Germany"),
        noNationalityReason = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(nationalityModel) = confirmation.nationality
    nationalityModel.content should be("<p>I am a citizen of Spain, France and Germany</p>")
    nationalityModel.editLink should be("/register-to-vote/forces/edit/nationality")
  }

  "In-progress application form with nationality excuse" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      nationality = Some(PartialNationality(
        british = None,
        irish = None,
        hasOtherCountry = None,
        otherCountries = List.empty,
        noNationalityReason = Some("I'm from Mars")
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(nationalityModel) = confirmation.nationality
    nationalityModel.content should be("<p>I cannot provide my nationality because:</p><p>I'm from Mars</p>")
    nationalityModel.editLink should be("/register-to-vote/forces/edit/nationality")
  }

  "In-progress application form with valid nino" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      nino = Some(Nino(
        nino = Some("AB123456C"),
        noNinoReason = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(ninoModel) = confirmation.nino
    ninoModel.content should be("<p>AB123456C</p>")
    ninoModel.editLink should be("/register-to-vote/forces/edit/nino")
  }

  "In-progress application form with nino excuse" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      nino = Some(Nino(
        nino = None,
        noNinoReason = Some("Recently arrived to the UK")
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(ninoModel) = confirmation.nino
    ninoModel.content should be("<p>I cannot provide my national insurance number because:</p>"+
      "<p>Recently arrived to the UK</p>")
    ninoModel.editLink should be("/register-to-vote/forces/edit/nino")
  }

  "In-progress application form with valid service and regiment" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      service = Some(Service(
        serviceName = Some(ServiceType.BritishArmy),
        regiment = Some("regiment")
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(serviceModel) = confirmation.service(false)
    serviceModel.content should be("<p>I am a member of the British Army</p><p>Regiment: regiment</p>")
    serviceModel.editLink should be("/register-to-vote/forces/edit/service")
  }

  "In-progress application form with valid service (no regiment)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      service = Some(Service(
        serviceName = Some(ServiceType.RoyalAirForce),
        regiment = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(serviceModel) = confirmation.service(false)
    serviceModel.content should be("<p>I am a member of the Royal Air Force</p>")
    serviceModel.editLink should be("/register-to-vote/forces/edit/service")
  }

  behavior of "ConfirmationBlocks.rank"
  "In-progress application form with a valid rank" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      rank = Some(Rank(
        serviceNumber = Some("123456"),
        rank = Some("Captain")
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(rankModel) = confirmation.rank
    rankModel.content should be("<p>Service number: 123456</p><p>Rank: Captain</p>")
    rankModel.editLink should be("/register-to-vote/forces/edit/rank")
  }


  "In-progress application form with valid UK address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      address = Some(PartialAddress(
        addressLine = Some("123 Fake Street"),
        uprn = Some("12345678"),
        postcode = "AB12 3CD",
        manualAddress = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(addressModel) = confirmation.address
    addressModel.content should be("<p>123 Fake Street</p><p>AB12 3CD</p>")
    addressModel.editLink should be("/register-to-vote/forces/edit/address/select")
  }

  "In-progress application form with valid UK manual address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      address = Some(PartialAddress(
        addressLine = None,
        uprn = None,
        postcode = "AB12 3CD",
        manualAddress = Some(PartialManualAddress(
          lineOne = Some("Unit 4, Elgar Business Centre"),
          lineTwo = Some("Moseley Road"),
          lineThree = Some("Hallow"),
          city = Some("Worcester")))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(addressModel) = confirmation.address
    addressModel.content should be("" +
      "<p>Unit 4, Elgar Business Centre, Moseley Road, Hallow, Worcester</p>" +
      "<p>AB12 3CD</p>")
    addressModel.editLink should be("/register-to-vote/forces/edit/address/manual")
  }


  "In-progress application form with valid previous UK address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      previousAddress = Some(PartialPreviousAddress(
        movedRecently = Some(MovedHouseOption.Yes),
        previousAddress = Some(PartialAddress(
          addressLine = Some("123 Fake Street"),
          uprn = Some("12345678"),
          postcode = "AB12 3CD",
          manualAddress = None
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(previousAddressModel) = confirmation.previousAddress
    previousAddressModel.content should be("<p>123 Fake Street</p><p>AB12 3CD</p>")
    previousAddressModel.editLink should be("/register-to-vote/forces/edit/previous-address")
  }

  "In-progress application form with valid previous UK manual address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      previousAddress = Some(PartialPreviousAddress(
        movedRecently = Some(MovedHouseOption.Yes),
        previousAddress = Some(PartialAddress(
          addressLine = None,
          uprn = None,
          postcode = "AB12 3CD",
          manualAddress = Some(PartialManualAddress(
            lineOne = Some("Unit 4, Elgar Business Centre"),
            lineTwo = Some("Moseley Road"),
            lineThree = Some("Hallow"),
            city = Some("Worcester")))
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(previousAddressModel) = confirmation.previousAddress
    previousAddressModel.content should be("" +
      "<p>Unit 4, Elgar Business Centre, Moseley Road, Hallow, Worcester</p>" +
      "<p>AB12 3CD</p>")
    previousAddressModel.editLink should be("/register-to-vote/forces/edit/previous-address")
  }

  "In-progress application form without previous UK address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      previousAddress = Some(PartialPreviousAddress(
        movedRecently = Some(MovedHouseOption.NotMoved),
        previousAddress = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(previousAddressModel) = confirmation.previousAddress
    previousAddressModel.content should be("<p>I have not moved in the last 12 months</p>")
    previousAddressModel.editLink should be("/register-to-vote/forces/edit/previous-address")
  }

  "In-progress application form with valid contact address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      address = Some(PartialAddress(
        addressLine = None,
        uprn = None,
        postcode = "AB12 3CD",
        manualAddress = Some(PartialManualAddress(
          lineOne = Some("Unit 4, Elgar Business Centre"),
          lineTwo = Some("Moseley Road"),
          lineThree = Some("Hallow"),
          city = Some("Worcester")))
      )),
      contactAddress = Some (PossibleContactAddresses(
        contactAddressType = Some("uk"),
        ukAddressLine = Some("my uk address, london"),
        bfpoContactAddress = None,
        otherContactAddress = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(contactAddressModel) = confirmation.contactAddress
    contactAddressModel.content should be(
      "<p>Unit 4, Elgar Business Centre, Moseley Road, Hallow, Worcester</p>" +
      "<p>AB12 3CD</p>")
    contactAddressModel.editLink should be("/register-to-vote/forces/edit/contact-address")
  }

  "In-progress application form with open register set to true" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      openRegisterOptin = Some(true)
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(openRegisterModel) = confirmation.openRegister
    openRegisterModel.content should be("<p>I want to include my details on the open register</p>")
    openRegisterModel.editLink should be("/register-to-vote/forces/edit/open-register")
  }

  "In-progress application form without open register flag" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      openRegisterOptin = Some(false)
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(openRegisterModel) = confirmation.openRegister
    openRegisterModel.content should be("<p>I don’t want to include my details on the open register</p>")
    openRegisterModel.editLink should be("/register-to-vote/forces/edit/open-register")
  }

  "application form with filled way to vote as in-person" should
    "generate confirmation mustache model with correctly rendered way to vote type" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      waysToVote = Some(WaysToVote(WaysToVoteType.InPerson))))
    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)
    val Some(model) = confirmation.waysToVote
    model.content should include("I want to vote in person, at a polling station")
    model.editLink should be("/register-to-vote/forces/edit/ways-to-vote")
  }


  "In-progress application form with postal vote (by post)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByPost)),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByPost,
        postalVoteOption = Some(true),
        deliveryMethod = Some(PostalVoteDeliveryMethod(
          deliveryMethod = Some("post"),
          emailAddress = None
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(postalOrProxyVoteModel) = confirmation.waysToVote
    postalOrProxyVoteModel.content should include("<p>I want to vote by post</p>")
    postalOrProxyVoteModel.content should include("Send me an application form in the post")
    postalOrProxyVoteModel.editLink should be("/register-to-vote/forces/edit/ways-to-vote")
  }

  "In-progress application form with postal vote (by email)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByPost)),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByPost,
        postalVoteOption = Some(true),
        deliveryMethod = Some(PostalVoteDeliveryMethod(
          deliveryMethod = Some("email"),
          emailAddress = Some("test@test.com")
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(postalOrProxyVoteModel) = confirmation.waysToVote
    postalOrProxyVoteModel.content should include("Send an application form to")
    postalOrProxyVoteModel.content should include("test@test.com")
    postalOrProxyVoteModel.editLink should be("/register-to-vote/forces/edit/ways-to-vote")
  }

  "In-progress application form with proxy vote (by post)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByProxy)),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByProxy,
        postalVoteOption = Some(true),
        deliveryMethod = Some(PostalVoteDeliveryMethod(
          deliveryMethod = Some("post"),
          emailAddress = None
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(postalOrProxyVoteModel) = confirmation.waysToVote
    postalOrProxyVoteModel.content should include("I want to vote by proxy (someone else voting for me)")
    postalOrProxyVoteModel.content should include("Send me an application form in the post")
    postalOrProxyVoteModel.editLink should be("/register-to-vote/forces/edit/ways-to-vote")
  }

  "In-progress application form with proxy vote (by email)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByProxy)),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByProxy,
        postalVoteOption = Some(true),
        deliveryMethod = Some(PostalVoteDeliveryMethod(
          deliveryMethod = Some("email"),
          emailAddress = Some("test@test.com")
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(postalOrProxyVoteModel) = confirmation.waysToVote
    postalOrProxyVoteModel.content should include("I want to vote by proxy (someone else voting for me)")
    postalOrProxyVoteModel.content should include("Send an application form to")
    postalOrProxyVoteModel.content should include("test@test.com")
    postalOrProxyVoteModel.editLink should be("/register-to-vote/forces/edit/ways-to-vote")
  }

  "In-progress application form without applying for postal vote" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByPost)),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByPost,
        postalVoteOption = Some(false),
        deliveryMethod = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(postalOrProxyVoteModel) = confirmation.waysToVote
    postalOrProxyVoteModel.content should include("I want to vote by post")
    postalOrProxyVoteModel.content should include("I do not need a postal vote application form")
    postalOrProxyVoteModel.editLink should be("/register-to-vote/forces/edit/ways-to-vote")
  }

  "In-progress application form without applying for proxy vote" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByProxy)),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByProxy,
        postalVoteOption = Some(false),
        deliveryMethod = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(postalOrProxyVoteModel) = confirmation.waysToVote
    postalOrProxyVoteModel.content should include("I want to vote by proxy")
    postalOrProxyVoteModel.content should include("I do not need a proxy vote application form")
    postalOrProxyVoteModel.editLink should be("/register-to-vote/forces/edit/ways-to-vote")
  }

  "In-progress application form with email contact" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      contact = Some(Contact(
        post = false,
        phone = None,
        email = Some(ContactDetail(true, Some("antoine@gds.com")))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(contactModel) = confirmation.contact
    contactModel.content should be("  <p>By email: antoine@gds.com</p>")
    contactModel.editLink should be("/register-to-vote/forces/edit/contact")
  }

  "In-progress application form with phone contact" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      contact = Some(Contact(
        post = false,
        phone = Some(ContactDetail(true, Some("+44 5678 907 546 ext. 3567-098"))),
        email = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(contactModel) = confirmation.contact
    contactModel.content should be(" <p>By phone: +44 5678 907 546 ext. 3567-098</p> ")
    contactModel.editLink should be("/register-to-vote/forces/edit/contact")
  }

  "In-progress application form with post contact" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      contact = Some(Contact(
        post = true,
        phone = None,
        email = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(contactModel) = confirmation.contact
    contactModel.content should be("<p>By post</p>  ")
    contactModel.editLink should be("/register-to-vote/forces/edit/contact")
  }

  behavior of "InProgressForm.confirmationNationalityString"

  it should "handle just irish checked" in {
    val form = confirmationForm.fillAndValidate(InprogressForces(
      nationality = Some(PartialNationality(
        british = None,
        irish = Some(true),
        hasOtherCountry = None,
        otherCountries = List.empty,
        noNationalityReason = None
      ))
    ))
    val confirmation = new ConfirmationBlocks(form)
    confirmation.confirmationNationalityString should be("I am Irish")
  }

  it should "handle just british checked" in {
    val form = confirmationForm.fillAndValidate(InprogressForces(
      nationality = Some(PartialNationality(
        british = Some(true),
        irish = None,
        hasOtherCountry = None,
        otherCountries = List.empty,
        noNationalityReason = None
      ))
    ))
    val confirmation = new ConfirmationBlocks(form)
    confirmation.confirmationNationalityString should be("I am British")
  }

  it should "handle british and irish checked" in {
    val form = confirmationForm.fillAndValidate(InprogressForces(
      nationality = Some(PartialNationality(
        british = Some(true),
        irish = Some(true),
        hasOtherCountry = None,
        otherCountries = List.empty,
        noNationalityReason = None
      ))
    ))
    val confirmation = new ConfirmationBlocks(form)
    confirmation.confirmationNationalityString should be("I am British and Irish")
  }

  it should "handle british, irish and an other nationality checked" in {
    val form = confirmationForm.fillAndValidate(InprogressForces(
      nationality = Some(PartialNationality(
        british = Some(true),
        irish = Some(true),
        hasOtherCountry = Some(true),
        otherCountries = List("New Zealand"),
        noNationalityReason = None
      ))
    ))
    val confirmation = new ConfirmationBlocks(form)
    confirmation.confirmationNationalityString should be(
      "I am British, Irish and a citizen of New Zealand"
    )
  }

  it should "handle british, irish and two other nationalities checked" in {
    val form = confirmationForm.fillAndValidate(InprogressForces(
      nationality = Some(PartialNationality(
        british = Some(true),
        irish = Some(true),
        hasOtherCountry = Some(true),
        otherCountries = List("New Zealand", "India"),
        noNationalityReason = None
      ))
    ))
    val confirmation = new ConfirmationBlocks(form)
    confirmation.confirmationNationalityString should be(
      "I am British, Irish and a citizen of New Zealand and India"
    )
  }

  it should "handle british, irish and three other nationalities checked" in {
    val form = confirmationForm.fillAndValidate(InprogressForces(
      nationality = Some(PartialNationality(
        british = Some(true),
        irish = Some(true),
        hasOtherCountry = Some(true),
        otherCountries = List("New Zealand", "India", "Japan"),
        noNationalityReason = None
      ))
    ))
    val confirmation = new ConfirmationBlocks(form)
    confirmation.confirmationNationalityString should be(
      "I am British, Irish and a citizen of New Zealand, India and Japan"
    )
  }

  it should "handle an other nationality checked" in {
    val form = confirmationForm.fillAndValidate(InprogressForces(
      nationality = Some(PartialNationality(
        british = None,
        irish = None,
        hasOtherCountry = Some(true),
        otherCountries = List("New Zealand"),
        noNationalityReason = None
      ))
    ))
    val confirmation = new ConfirmationBlocks(form)
    confirmation.confirmationNationalityString should be(
      "I am a citizen of New Zealand"
    )
  }

  it should "handle an three other nationalities checked" in {
    val form = confirmationForm.fillAndValidate(InprogressForces(
      nationality = Some(PartialNationality(
        british = None,
        irish = None,
        hasOtherCountry = Some(true),
        otherCountries = List("New Zealand", "India", "Japan"),
        noNationalityReason = None
      ))
    ))
    val confirmation = new ConfirmationBlocks(form)
    confirmation.confirmationNationalityString should be(
      "I am a citizen of New Zealand, India and Japan"
    )
  }

  it should "handle two other nationalities checked" in {
    val form = confirmationForm.fillAndValidate(InprogressForces(
      nationality = Some(PartialNationality(
        british = None,
        irish = None,
        hasOtherCountry = Some(true),
        otherCountries = List("New Zealand", "India"),
        noNationalityReason = None
      ))
    ))
    val confirmation = new ConfirmationBlocks(form)
    confirmation.confirmationNationalityString should be(
      "I am a citizen of New Zealand and India"
    )
  }
}
