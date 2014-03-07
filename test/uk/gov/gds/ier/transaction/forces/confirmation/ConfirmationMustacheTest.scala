package uk.gov.gds.ier.transaction.forces.confirmation

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.InprogressForces
import uk.gov.gds.ier.model.Name
import uk.gov.gds.ier.validation.InProgressForm
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
      form = InProgressForm(partiallyFilledApplicationForm)
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
      form = InProgressForm(partiallyFilledApplicationForm)
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
      form = InProgressForm(partiallyFilledApplicationForm)
    )

    displayPartnerBlock should be (true)
  }


  "In-progress application form with filled name" should
    "generate confirmation mustache model with correctly rendered names and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      name = Some(Name(
        firstName = "John",
        middleNames = None,
        lastName = "Smith"))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(nameModel) = confirmation.name
    nameModel.content should be("<p>John Smith</p>")
    nameModel.editLink should be("/register-to-vote/forces/edit/name")

  }

  "In-progress application form with filled name with middle names" should
    "generate confirmation mustache model with correctly rendered names and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      name = Some(Name(
        firstName = "John",
        middleNames = Some("Walker Junior"),
        lastName = "Smith"))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(nameModel) = confirmation.name
    nameModel.content should be("<p>John Walker Junior Smith</p>")
    nameModel.editLink should be("/register-to-vote/forces/edit/name")
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

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

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

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

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

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(nationalityModel) = confirmation.nationality
    nationalityModel.content should be("<p>I am a citizen of United Kingdom</p>")
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

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(nationalityModel) = confirmation.nationality
    nationalityModel.content should be("<p>I am a citizen of Ireland</p>")
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

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

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

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

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

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

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

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

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

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(serviceModel) = confirmation.service
    serviceModel.content should be("<p>I am a member of the Army</p><p>Regiment: regiment</p>")
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

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(serviceModel) = confirmation.service
    serviceModel.content should be("<p>I am a member of the Royal Airforce</p>")
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

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

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

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

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
        manualAddress = Some("my totally fake manual address, 123")
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(addressModel) = confirmation.address
    addressModel.content should be("<p>my totally fake manual address, 123</p><p>AB12 3CD</p>")
    addressModel.editLink should be("/register-to-vote/forces/edit/address/manual")
  }

  "In-progress application form with valid contact address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      address = Some(PartialAddress(
        addressLine = None,
        uprn = None,
        postcode = "AB12 3CD",
        manualAddress = Some("my totally fake manual address, 123")
      )),
      contactAddress = Some (PossibleContactAddresses(
        contactAddressType = Some("uk"),
        ukAddressLine = Some("my uk address, london"),
        bfpoContactAddress = None,
        otherContactAddress = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(contactAddressModel) = confirmation.contactAddress
    contactAddressModel.content should be("<p>my totally fake manual address, 123</p><p>AB12 3CD</p>")
    contactAddressModel.editLink should be("/register-to-vote/forces/edit/contact-address")
  }

  "In-progress application form with open register set to true" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      openRegisterOptin = Some(true)
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(openRegisterModel) = confirmation.openRegister
    openRegisterModel.content should be("<p>I want to include my details on the open register</p>")
    openRegisterModel.editLink should be("/register-to-vote/forces/edit/open-register")
  }

  "In-progress application form without open register flag" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      openRegisterOptin = Some(false)
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(openRegisterModel) = confirmation.openRegister
    openRegisterModel.content should be("<p>I don’t want to include my details on the open register</p>")
    openRegisterModel.editLink should be("/register-to-vote/forces/edit/open-register")
  }


  "application form with filled way to vote as by-post" should
    "generate confirmation mustache model with correctly rendered way to vote type" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByPost))))
    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))
    val Some(nameModel) = confirmation.waysToVote
    nameModel.content should be("<p>By post</p>")
  }

  "application form with filled way to vote as by-proxy" should
    "generate confirmation mustache model with correctly rendered way to vote type" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByProxy))))
    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))
    val Some(nameModel) = confirmation.waysToVote
    nameModel.content should be("<p>By proxy (someone else voting for you)</p>")
  }

  "application form with filled way to vote as in-person" should
    "generate confirmation mustache model with correctly rendered way to vote type" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      waysToVote = Some(WaysToVote(WaysToVoteType.InPerson))))
    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))
    val Some(nameModel) = confirmation.waysToVote
    nameModel.content should be("<p>In the UK, at a polling station</p>")
  }

  it should "return none (waysToVote not answered)" in {
    val partialApplication = confirmationForm

    val confirmation = new ConfirmationBlocks(InProgressForm(partialApplication))
    val model = confirmation.postalOrProxyVote

    model.isDefined should be(false)
  }

  "In-progress application form with postal vote (by post)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByPost,
        postalVoteOption = Some(true),
        deliveryMethod = Some(PostalVoteDeliveryMethod(
          deliveryMethod = Some("post"),
          emailAddress = None
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(postalOrProxyVoteModel) = confirmation.postalOrProxyVote
    postalOrProxyVoteModel.content should be("<p>Please post me a postal vote application form</p>")
    postalOrProxyVoteModel.editLink should be("/register-to-vote/forces/edit/postal-vote")
  }

  "In-progress application form with postal vote (by email)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByPost,
        postalVoteOption = Some(true),
        deliveryMethod = Some(PostalVoteDeliveryMethod(
          deliveryMethod = Some("email"),
          emailAddress = Some("antoine@gds.com")
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(postalOrProxyVoteModel) = confirmation.postalOrProxyVote
    postalOrProxyVoteModel.content should be("<p>Please email a postal vote application form to:</p><p>antoine@gds.com</p>")
    postalOrProxyVoteModel.editLink should be("/register-to-vote/forces/edit/postal-vote")
  }

  "In-progress application form with proxy vote (by post)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByProxy,
        postalVoteOption = Some(true),
        deliveryMethod = Some(PostalVoteDeliveryMethod(
          deliveryMethod = Some("post"),
          emailAddress = None
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(postalOrProxyVoteModel) = confirmation.postalOrProxyVote
    postalOrProxyVoteModel.content should be("<p>Please post me a proxy vote application form</p>")
    postalOrProxyVoteModel.editLink should be("/register-to-vote/forces/edit/proxy-vote")
  }

  "In-progress application form with proxy vote (by email)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByProxy,
        postalVoteOption = Some(true),
        deliveryMethod = Some(PostalVoteDeliveryMethod(
          deliveryMethod = Some("email"),
          emailAddress = Some("antoine@gds.com")
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(postalOrProxyVoteModel) = confirmation.postalOrProxyVote
    postalOrProxyVoteModel.content should be("<p>Please email a proxy vote application form to:</p><p>antoine@gds.com</p>")
    postalOrProxyVoteModel.editLink should be("/register-to-vote/forces/edit/proxy-vote")
  }

  "In-progress application form without applying for postal vote" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByPost,
        postalVoteOption = Some(false),
        deliveryMethod = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(postalOrProxyVoteModel) = confirmation.postalOrProxyVote
    postalOrProxyVoteModel.content should be("<p>I do not need a postal vote application form</p>")
    postalOrProxyVoteModel.editLink should be("/register-to-vote/forces/edit/postal-vote")
  }

  "In-progress application form without applying for proxy vote" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByProxy,
        postalVoteOption = Some(false),
        deliveryMethod = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(postalOrProxyVoteModel) = confirmation.postalOrProxyVote
    postalOrProxyVoteModel.content should be("<p>I do not need a proxy vote application form</p>")
    postalOrProxyVoteModel.editLink should be("/register-to-vote/forces/edit/proxy-vote")
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

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

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

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

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

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(contactModel) = confirmation.contact
    contactModel.content should be("<p>By post</p>  ")
    contactModel.editLink should be("/register-to-vote/forces/edit/contact")
  }
}
