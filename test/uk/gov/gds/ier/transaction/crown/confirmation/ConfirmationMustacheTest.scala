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
        crownServant = true,
        crownPartner = false,
        councilEmployee = false,
        councilPartner = false
      ))
    ))

    val displayPartnerBlock = Confirmation.confirmationData(
      form = InProgressForm(partiallyFilledApplicationForm),
      backUrl = "http://backUrl",
      postUrl = "http://postUrl"
    ).displayPartnerBlock

    displayPartnerBlock should be (false)
  }

  "In-progress application form without a crown or council partner (member and partner = true)" should
    "generate confirmation mustache model without partner block" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      statement = Some(CrownStatement(
        crownServant = true,
        crownPartner = true,
        councilEmployee = false,
        councilPartner = false
      ))
    ))

    val displayPartnerBlock = Confirmation.confirmationData(
      form = InProgressForm(partiallyFilledApplicationForm),
      backUrl = "http://backUrl",
      postUrl = "http://postUrl"
    ).displayPartnerBlock

    displayPartnerBlock should be (false)
  }

  "In-progress application form with a crown partner" should
    "generate confirmation mustache model with partner block" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      statement = Some(CrownStatement(
        crownServant = false,
        crownPartner = true,
        councilEmployee = false,
        councilPartner = false
      ))
    ))

    val displayPartnerBlock = Confirmation.confirmationData(
      form = InProgressForm(partiallyFilledApplicationForm),
      backUrl = "http://backUrl",
      postUrl = "http://postUrl"
    ).displayPartnerBlock

    displayPartnerBlock should be (true)
  }




  "In-progress application form without a crown or council partner (BC member)" should
    "generate confirmation mustache model without partner block" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      statement = Some(CrownStatement(
        crownServant = false,
        crownPartner = false,
        councilEmployee = true,
        councilPartner = false
      ))
    ))

    val displayPartnerBlock = Confirmation.confirmationData(
      form = InProgressForm(partiallyFilledApplicationForm),
      backUrl = "http://backUrl",
      postUrl = "http://postUrl"
    ).displayPartnerBlock

    displayPartnerBlock should be (false)
  }

  "In-progress application form without a crown or council partner (BC member and partner = true)" should
    "generate confirmation mustache model without partner block" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      statement = Some(CrownStatement(
        crownServant = false,
        crownPartner = false,
        councilEmployee = true,
        councilPartner = true
      ))
    ))

    val displayPartnerBlock = Confirmation.confirmationData(
      form = InProgressForm(partiallyFilledApplicationForm),
      backUrl = "http://backUrl",
      postUrl = "http://postUrl"
    ).displayPartnerBlock

    displayPartnerBlock should be (false)
  }

  "In-progress application form with a BC partner" should
    "generate confirmation mustache model with partner block" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      statement = Some(CrownStatement(
        crownServant = false,
        crownPartner = false,
        councilEmployee = false,
        councilPartner = true
      ))
    ))

    val displayPartnerBlock = Confirmation.confirmationData(
      form = InProgressForm(partiallyFilledApplicationForm),
      backUrl = "http://backUrl",
      postUrl = "http://postUrl"
    ).displayPartnerBlock

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

    val jobTitleModel = confirmation.jobTitle

    jobTitleModel.content should be("<p>some job title</p><p>MoJ</p>")
    jobTitleModel.editLink should be("/register-to-vote/crown/edit/job-title")
  }

  behavior of "ConfirmationBlocks.partnerJobTitle"

  it should "return jobTitle if displayPartnerBlock = true (councilPartner)" in {
    val application = confirmationForm.fillAndValidate(InprogressCrown(
      job = Some(Job(
        jobTitle = Some("some job title"),
        govDepartment = Some("department")
      )),
      statement = Some(CrownStatement(
        crownServant = false,
        crownPartner = false,
        councilEmployee = false,
        councilPartner = true
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(application))

    val Some(jobTitleModel) = confirmation.partnerJobTitle

    jobTitleModel.content should be("<p>some job title</p><p>department</p>")
  }

  it should "return jobTitle if displayPartnerBlock = true (crownPartner)" in {
    val application = confirmationForm.fillAndValidate(InprogressCrown(
      job = Some(Job(
        jobTitle = Some("some job title"),
        govDepartment = Some("department")
      )),
      statement = Some(CrownStatement(
        crownServant = false,
        crownPartner = true,
        councilEmployee = false,
        councilPartner = false
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(application))

    val Some(jobTitleModel) = confirmation.partnerJobTitle

    jobTitleModel.content should be("<p>some job title</p><p>department</p>")
  }

  it should "return None if displayPartnerBlock = false (crownServant)" in {
    val application = confirmationForm.fillAndValidate(InprogressCrown(
      job = None,
      statement = Some(CrownStatement(
        crownServant = true,
        crownPartner = false,
        councilEmployee = false,
        councilPartner = false
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(application))

    confirmation.partnerJobTitle should be(None)
  }

  it should "return completethis jobTitle if displayPartnerBlock = true (crownPartner)" in {
    val application = confirmationForm.fillAndValidate(InprogressCrown(
      job = None,
      statement = Some(CrownStatement(
        crownServant = false,
        crownPartner = true,
        councilEmployee = false,
        councilPartner = false
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(application))

    val Some(jobTitleModel) = confirmation.partnerJobTitle

    jobTitleModel.content should include("Please complete this step")
  }

  behavior of "ConfirmationBlocks.applicantJobTitle"

  it should "return jobTitle if displayPartnerBlock = false (councilEmployee)" in {
    val application = confirmationForm.fillAndValidate(InprogressCrown(
      job = Some(Job(
        jobTitle = Some("some job title"),
        govDepartment = Some("department")
      )),
      statement = Some(CrownStatement(
        crownServant = false,
        crownPartner = false,
        councilEmployee = true,
        councilPartner = false
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(application))

    val Some(jobTitleModel) = confirmation.applicantJobTitle

    jobTitleModel.content should be("<p>some job title</p><p>department</p>")
  }

  it should "return jobTitle if displayPartnerBlock = false (crownServant)" in {
    val application = confirmationForm.fillAndValidate(InprogressCrown(
      job = Some(Job(
        jobTitle = Some("some job title"),
        govDepartment = Some("department")
      )),
      statement = Some(CrownStatement(
        crownServant = true,
        crownPartner = false,
        councilEmployee = false,
        councilPartner = false
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(application))

    val Some(jobTitleModel) = confirmation.applicantJobTitle

    jobTitleModel.content should be("<p>some job title</p><p>department</p>")
  }

  it should "return None if displayPartnerBlock = true (crownServant)" in {
    val application = confirmationForm.fillAndValidate(InprogressCrown(
      job = None,
      statement = Some(CrownStatement(
        crownServant = false,
        crownPartner = true,
        councilEmployee = false,
        councilPartner = false
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(application))

    confirmation.applicantJobTitle should be(None)
  }

  it should "return completethis jobTitle if displayPartnerBlock = false (crownServant)" in {
    val application = confirmationForm.fillAndValidate(InprogressCrown(
      job = None,
      statement = Some(CrownStatement(
        crownServant = true,
        crownPartner = false,
        councilEmployee = false,
        councilPartner = false
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(application))

    val Some(jobTitleModel) = confirmation.applicantJobTitle

    jobTitleModel.content should include("Please complete this step")
  }

  it should "prefer applicant when conflicting answers (crownServant & councilPartner)" in {

    val application = confirmationForm.fillAndValidate(InprogressCrown(
      statement = Some(CrownStatement(
        crownServant = true,
        crownPartner = false,
        councilEmployee = false,
        councilPartner = true
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(application))

    confirmation.applicantJobTitle.isDefined should be(true)
    confirmation.partnerJobTitle should be(None)

    val displayPartnerBlock = Confirmation.confirmationData(
      form = InProgressForm(application),
      backUrl = "http://backUrl",
      postUrl = "http://postUrl"
    ).displayPartnerBlock

    displayPartnerBlock should be(false)
  }

  it should "prefer applicant when conflicting answers (crownPartner & councilEmployee)" in {

    val application = confirmationForm.fillAndValidate(InprogressCrown(
      statement = Some(CrownStatement(
        crownServant = false,
        crownPartner = true,
        councilEmployee = true,
        councilPartner = false
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(application))

    confirmation.applicantJobTitle.isDefined should be(true)
    confirmation.partnerJobTitle should be(None)

    val displayPartnerBlock = Confirmation.confirmationData(
      form = InProgressForm(application),
      backUrl = "http://backUrl",
      postUrl = "http://postUrl"
    ).displayPartnerBlock

    displayPartnerBlock should be(false)
  }


  "In-progress application form with valid UK address (hasUkAddress = true)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    // this test also (unintentionally?) test that if both selected and manual address are present
    // in application user is redirected to edit the selected address rather that the manual one
    // because edit link should take user to the displayed variant, that is selected address
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(

      address = Some(LastUkAddress(
        hasUkAddress = Some(true),
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
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(addressModel) = confirmation.address
    addressModel.title should be("Your UK address")
    addressModel.content should be("<p>123 Fake Street</p><p>AB12 3CD</p>")
    addressModel.editLink should be("/register-to-vote/crown/edit/address/first")
  }

  "In-progress application form with valid UK address (hasUkAddress = false)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {

    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(

      address = Some(LastUkAddress(
        hasUkAddress = Some(false),
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
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(addressModel) = confirmation.address
    addressModel.title should be("Your last UK address")
    addressModel.content should be("<p>123 Fake Street</p><p>AB12 3CD</p>")
    addressModel.editLink should be("/register-to-vote/crown/edit/address/first")
  }

  "In-progress application form with valid UK address (hasUkAddress = None)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {

    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(

      address = Some(LastUkAddress(
        hasUkAddress = None,
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
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(addressModel) = confirmation.address
    addressModel.title should be("Your last UK address")
    addressModel.content should be("<p>123 Fake Street</p><p>AB12 3CD</p>")
    addressModel.editLink should be("/register-to-vote/crown/edit/address/first")
  }

  "In-progress application form with valid UK manual address (hasUkAddress = true)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      address = Some(LastUkAddress(
        hasUkAddress = Some(true),
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
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(addressModel) = confirmation.address
    addressModel.title should be("Your UK address")
    addressModel.content should be("" +
      "<p>Unit 4, Elgar Business Centre, Moseley Road, Hallow, Worcester</p>" +
      "<p>AB12 3CD</p>")
    addressModel.editLink should be("/register-to-vote/crown/edit/address/first")
  }

  "In-progress application form with valid UK manual address (hasUkAddress = false)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      address = Some(LastUkAddress(
        hasUkAddress = Some(false),
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
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(addressModel) = confirmation.address
    addressModel.title should be("Your last UK address")
    addressModel.content should be("" +
      "<p>Unit 4, Elgar Business Centre, Moseley Road, Hallow, Worcester</p>" +
      "<p>AB12 3CD</p>")
    addressModel.editLink should be("/register-to-vote/crown/edit/address/first")
  }

  "In-progress application form with valid UK manual address (hasUkAddress = None)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      address = Some(LastUkAddress(
        hasUkAddress = None,
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
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(addressModel) = confirmation.address
    addressModel.title should be("Your last UK address")
    addressModel.content should be("" +
      "<p>Unit 4, Elgar Business Centre, Moseley Road, Hallow, Worcester</p>" +
      "<p>AB12 3CD</p>")
    addressModel.editLink should be("/register-to-vote/crown/edit/address/first")
  }

  "In-progress application form with valid contact address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      address = Some(LastUkAddress(
        hasUkAddress = Some(true),
        address = Some(PartialAddress(
          addressLine = None,
          uprn = None,
          postcode = "AB12 3CD",
          manualAddress = Some(PartialManualAddress(
            lineOne = Some("my totally fake manual address"),
            lineTwo = Some("123"),
            lineThree = None,
            city = Some("Fakebury")
          ))
        ))
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
    contactAddressModel.content should be("<p>my totally fake manual address, 123, Fakebury</p><p>AB12 3CD</p>")
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


  "application form with filled way to vote as by-proxy" should
    "generate confirmation mustache model with correctly rendered way to vote type" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
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
    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))
    val Some(model) = confirmation.waysToVote
    model.content should include("I want to vote by proxy (someone else voting for me)")
    model.editLink should be("/register-to-vote/crown/edit/ways-to-vote")
  }

  "application form with filled way to vote as in-person" should
    "generate confirmation mustache model with correctly rendered way to vote type" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      waysToVote = Some(WaysToVote(WaysToVoteType.InPerson))))
    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))
    val Some(model) = confirmation.waysToVote
    model.content should include("I want to vote in person, at a polling station")
    model.editLink should be("/register-to-vote/crown/edit/ways-to-vote")
  }


  "In-progress application form with postal vote (by post)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
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

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(postalOrProxyVoteModel) = confirmation.waysToVote
    postalOrProxyVoteModel.content should include("<p>I want to vote by post</p>")
    postalOrProxyVoteModel.content should include("Send me an application form in the post")
    postalOrProxyVoteModel.editLink should be("/register-to-vote/crown/edit/ways-to-vote")
  }

  "In-progress application form with postal vote (by email)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
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

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(postalOrProxyVoteModel) = confirmation.waysToVote
    postalOrProxyVoteModel.content should include("Send an application form to")
    postalOrProxyVoteModel.content should include("test@test.com")
    postalOrProxyVoteModel.editLink should be("/register-to-vote/crown/edit/ways-to-vote")
  }

  "In-progress application form with proxy vote (by post)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
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

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(postalOrProxyVoteModel) = confirmation.waysToVote
    postalOrProxyVoteModel.content should include("I want to vote by proxy (someone else voting for me)")
    postalOrProxyVoteModel.content should include("Send me an application form in the post")
    postalOrProxyVoteModel.editLink should be("/register-to-vote/crown/edit/ways-to-vote")
  }

  "In-progress application form with proxy vote (by email)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
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

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(postalOrProxyVoteModel) = confirmation.waysToVote
    postalOrProxyVoteModel.content should include("I want to vote by proxy (someone else voting for me)")
    postalOrProxyVoteModel.content should include("Send an application form to")
    postalOrProxyVoteModel.content should include("test@test.com")
    postalOrProxyVoteModel.editLink should be("/register-to-vote/crown/edit/ways-to-vote")
  }

  "In-progress application form without applying for postal vote" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByPost)),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByPost,
        postalVoteOption = Some(false),
        deliveryMethod = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(postalOrProxyVoteModel) = confirmation.waysToVote
    postalOrProxyVoteModel.content should include("I want to vote by post")
    postalOrProxyVoteModel.content should include("I do not need a postal vote application form")
    postalOrProxyVoteModel.editLink should be("/register-to-vote/crown/edit/ways-to-vote")
  }

  "In-progress application form without applying for proxy vote" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressCrown(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByProxy)),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByProxy,
        postalVoteOption = Some(false),
        deliveryMethod = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))

    val Some(postalOrProxyVoteModel) = confirmation.waysToVote
    postalOrProxyVoteModel.content should include("I want to vote by proxy")
    postalOrProxyVoteModel.content should include("I do not need a proxy vote application form")
    postalOrProxyVoteModel.editLink should be("/register-to-vote/crown/edit/ways-to-vote")
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
