package uk.gov.gds.ier.transaction.crown.confirmation

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.InprogressCrown
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

  "In-progress application form without a crown or council partner" should
    "generate confirmation mustache model without partner block" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      statement = Some(CrownStatement(
        crownMember = Some(true),
        partnerCrownMember = None,
        britishCouncilMember = None,
        partnerBritishCouncilMember = None
      ))
    ))

    val displayPartnerBlock = Confirmation.displayPartnerBlock(
      form = InProgressForm(partiallyFilledApplicationForm)
    )

    displayPartnerBlock should be (false)
  }

  "In-progress application form without a crown or council partner (member and partner = true)" should
    "generate confirmation mustache model without partner block" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      statement = Some(CrownStatement(
        crownMember = Some(true),
        partnerCrownMember = Some(true),
        britishCouncilMember = None,
        partnerBritishCouncilMember = None
      ))
    ))

    val displayPartnerBlock = Confirmation.displayPartnerBlock(
      form = InProgressForm(partiallyFilledApplicationForm)
    )

    displayPartnerBlock should be (false)
  }

  "In-progress application form with a crown partner" should
    "generate confirmation mustache model with partner block" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      statement = Some(CrownStatement(
        crownMember = None,
        partnerCrownMember = Some(true),
        britishCouncilMember = None,
        partnerBritishCouncilMember = None
      ))
    ))

    val displayPartnerBlock = Confirmation.displayPartnerBlock(
      form = InProgressForm(partiallyFilledApplicationForm)
    )

    displayPartnerBlock should be (true)
  }




  "In-progress application form without a crown or council partner (BC member)" should
    "generate confirmation mustache model without partner block" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      statement = Some(CrownStatement(
        crownMember = None,
        partnerCrownMember = None,
        britishCouncilMember = Some(true),
        partnerBritishCouncilMember = None
      ))
    ))

    val displayPartnerBlock = Confirmation.displayPartnerBlock(
      form = InProgressForm(partiallyFilledApplicationForm)
    )

    displayPartnerBlock should be (false)
  }

  "In-progress application form without a crown or council partner (BC member and partner = true)" should
    "generate confirmation mustache model without partner block" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      statement = Some(CrownStatement(
        crownMember = None,
        partnerCrownMember = None,
        britishCouncilMember = Some(true),
        partnerBritishCouncilMember = Some(true)
      ))
    ))

    val displayPartnerBlock = Confirmation.displayPartnerBlock(
      form = InProgressForm(partiallyFilledApplicationForm)
    )

    displayPartnerBlock should be (false)
  }

  "In-progress application form with a BC partner" should
    "generate confirmation mustache model with partner block" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      statement = Some(CrownStatement(
        crownMember = None,
        partnerCrownMember = None,
        britishCouncilMember = None,
        partnerBritishCouncilMember = Some(true)
      ))
    ))

    val displayPartnerBlock = Confirmation.displayPartnerBlock(
      form = InProgressForm(partiallyFilledApplicationForm)
    )

    displayPartnerBlock should be (true)
  }

  "In-progress application form with filled name and previous name" should
    "generate confirmation mustache model with correctly rendered names and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
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

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(nameModel) = confirmation.name
    nameModel.content should be("<p>John Smith</p>")
    nameModel.editLink should be("/register-to-vote/crown/edit/name")

    val Some(prevNameModel) = confirmation.previousName
    prevNameModel.content should be("<p>Jan Kovar</p>")
    prevNameModel.editLink should be("/register-to-vote/crown/edit/name")
  }

  "In-progress application form with filled name and previous name with middle names" should
    "generate confirmation mustache model with correctly rendered names and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
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

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(nameModel) = confirmation.name
    nameModel.content should be("<p>John Walker Junior Smith</p>")
    nameModel.editLink should be("/register-to-vote/crown/edit/name")

    val Some(prevNameModel) = confirmation.previousName
    prevNameModel.content should be("<p>Jan Janko Janik Kovar</p>")
    prevNameModel.editLink should be("/register-to-vote/crown/edit/name")
  }

  "In-progress application form with filled date of birth" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
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
    dateOfBirthModel.editLink should be("/register-to-vote/crown/edit/date-of-birth")
  }

  "In-progress application form with filled date of birth excuse" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
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
    dateOfBirthModel.editLink should be("/register-to-vote/crown/edit/date-of-birth")
  }

  "In-progress application form with british nationality" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {

    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(completeCrownApplication.copy(
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
    nationalityModel.editLink should be("/register-to-vote/crown/edit/nationality")
  }

  "In-progress application form with irish nationality" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(completeCrownApplication.copy(
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
    nationalityModel.editLink should be("/register-to-vote/crown/edit/nationality")
  }

  "In-progress application form with other nationality" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(completeCrownApplication.copy(
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
    nationalityModel.editLink should be("/register-to-vote/crown/edit/nationality")
  }

  "In-progress application form with nationality excuse" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
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
    nationalityModel.editLink should be("/register-to-vote/crown/edit/nationality")
  }

  "In-progress application form with valid nino" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      nino = Some(Nino(
        nino = Some("AB123456C"),
        noNinoReason = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(ninoModel) = confirmation.nino
    ninoModel.content should be("<p>AB123456C</p>")
    ninoModel.editLink should be("/register-to-vote/crown/edit/nino")
  }

  "In-progress application form with nino excuse" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      nino = Some(Nino(
        nino = None,
        noNinoReason = Some("Recently arrived to the UK")
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(ninoModel) = confirmation.nino
    ninoModel.content should be("<p>I cannot provide my national insurance number because:</p>"+
      "<p>Recently arrived to the UK</p>")
    ninoModel.editLink should be("/register-to-vote/crown/edit/nino")
  }

  "In-progress application form with valid job" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      job = Some(Job(
        jobTitle = Some("some job title"),
        govDepartment = Some("MoJ")
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(jobTitleModel) = confirmation.jobTitle

    jobTitleModel.content should be("<p>some job title</p><p>MoJ</p>")
    jobTitleModel.editLink should be("/register-to-vote/crown/edit/job-title")
  }

  "In-progress application form with valid UK address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    // this test also (unintentionally?) test that if both selected and manual address are present
    // in application user is redirected to edit the selected address rather that the manual one
    // because edit link should take user to the displayed variant, that is selected address
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      address = Some(PartialAddress(
        addressLine = Some("123 Fake Street"),
        uprn = Some("12345678"),
        postcode = "AB12 3CD",
        manualAddress = Some(PartialManualAddress(
          lineOne = Some("Unit 4, Elgar Business Centre"),
          lineTwo = Some("Moseley Road"),
          lineThree = Some("Hallow"),
          city = Some("Worcester")))
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(addressModel) = confirmation.address
    addressModel.content should be("<p>123 Fake Street</p><p>AB12 3CD</p>")
    addressModel.editLink should be("/register-to-vote/crown/edit/address/select")
  }

  "In-progress application form with valid UK manual address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
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

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(addressModel) = confirmation.address
    addressModel.content should be("" +
      "<p>Unit 4, Elgar Business Centre, Moseley Road, Hallow, Worcester</p>" +
      "<p>AB12 3CD</p>")
    addressModel.editLink should be("/register-to-vote/crown/edit/address/manual")
  }

  "In-progress application form with valid contact address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
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
    contactAddressModel.editLink should be("/register-to-vote/crown/edit/contact-address")
  }

  "In-progress application form with open register set to true" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      openRegisterOptin = Some(true)
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(openRegisterModel) = confirmation.openRegister
    openRegisterModel.content should be("<p>I want to include my details on the open register</p>")
    openRegisterModel.editLink should be("/register-to-vote/crown/edit/open-register")
  }

  "In-progress application form without open register flag" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      openRegisterOptin = Some(false)
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(openRegisterModel) = confirmation.openRegister
    openRegisterModel.content should be("<p>I don’t want to include my details on the open register</p>")
    openRegisterModel.editLink should be("/register-to-vote/crown/edit/open-register")
  }


  "application form with filled way to vote as by-post" should
    "generate confirmation mustache model with correctly rendered way to vote type" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByPost))))
    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))
    val Some(nameModel) = confirmation.waysToVote
    nameModel.content should be("<p>By post</p>")
  }

  "application form with filled way to vote as by-proxy" should
    "generate confirmation mustache model with correctly rendered way to vote type" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByProxy))))
    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))
    val Some(nameModel) = confirmation.waysToVote
    nameModel.content should be("<p>By proxy (someone else voting for you)</p>")
  }

  "application form with filled way to vote as in-person" should
    "generate confirmation mustache model with correctly rendered way to vote type" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
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
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
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
    postalOrProxyVoteModel.editLink should be("/register-to-vote/crown/edit/postal-vote")
  }

  "In-progress application form with postal vote (by email)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
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
    postalOrProxyVoteModel.editLink should be("/register-to-vote/crown/edit/postal-vote")
  }

  "In-progress application form with proxy vote (by post)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
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
    postalOrProxyVoteModel.editLink should be("/register-to-vote/crown/edit/proxy-vote")
  }

  "In-progress application form with proxy vote (by email)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
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
    postalOrProxyVoteModel.editLink should be("/register-to-vote/crown/edit/proxy-vote")
  }

  "In-progress application form without applying for postal vote" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByPost,
        postalVoteOption = Some(false),
        deliveryMethod = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(postalOrProxyVoteModel) = confirmation.postalOrProxyVote
    postalOrProxyVoteModel.content should be("<p>I do not need a postal vote application form</p>")
    postalOrProxyVoteModel.editLink should be("/register-to-vote/crown/edit/postal-vote")
  }

  "In-progress application form without applying for proxy vote" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByProxy,
        postalVoteOption = Some(false),
        deliveryMethod = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(postalOrProxyVoteModel) = confirmation.postalOrProxyVote
    postalOrProxyVoteModel.content should be("<p>I do not need a proxy vote application form</p>")
    postalOrProxyVoteModel.editLink should be("/register-to-vote/crown/edit/proxy-vote")
  }

  "In-progress application form with email contact" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      contact = Some(Contact(
        post = false,
        phone = None,
        email = Some(ContactDetail(true, Some("antoine@gds.com")))
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(contactModel) = confirmation.contact
    contactModel.content should be("  <p>By email: antoine@gds.com</p>")
    contactModel.editLink should be("/register-to-vote/crown/edit/contact")
  }

  "In-progress application form with phone contact" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      contact = Some(Contact(
        post = false,
        phone = Some(ContactDetail(true, Some("+44 5678 907 546 ext. 3567-098"))),
        email = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(contactModel) = confirmation.contact
    contactModel.content should be(" <p>By phone: +44 5678 907 546 ext. 3567-098</p> ")
    contactModel.editLink should be("/register-to-vote/crown/edit/contact")
  }

  "In-progress application form with post contact" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      contact = Some(Contact(
        post = true,
        phone = None,
        email = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(contactModel) = confirmation.contact
    contactModel.content should be("<p>By post</p>  ")
    contactModel.editLink should be("/register-to-vote/crown/edit/contact")
  }
}
