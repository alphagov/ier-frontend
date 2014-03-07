package uk.gov.gds.ier.transaction.forces.nationality

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import scala.Some
import controllers.step.forces.routes._
import uk.gov.gds.ier.model.{PartialNationality, InprogressForces}

class NationalityMustacheTest
  extends FlatSpec
  with Matchers
  with NationalityForms
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val nationalityMustache = new NationalityMustache {}

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = nationalityForm
    val nationalityModel = nationalityMustache.transformFormStepToMustacheData(
      emptyApplicationForm, NationalityController.post, Some(AddressController.get))

    nationalityModel.question.title should be("What is your nationality?")
    nationalityModel.question.postUrl should be("/register-to-vote/forces/nationality")
    nationalityModel.question.backUrl should be("/register-to-vote/forces/address")

    nationalityModel.britishOption.attributes should be("")
    nationalityModel.irishOption.attributes should be("")
    nationalityModel.hasOtherCountryOption.attributes should be("")
    nationalityModel.otherCountriesHead.value should be("")
    nationalityModel.otherCountriesTail.isEmpty should be(true)
    nationalityModel.moreThanOneOtherCountry should be(false)
    nationalityModel.noNationalityReason.value should be("")


  }

  it should "progress form with british option should produce Mustache Model with values present" in {
    val partiallyFilledApplicationForm = nationalityForm.fill(InprogressForces(
      nationality = Some(PartialNationality(
        british = Some(true),
        irish = None,
        hasOtherCountry = None,
        otherCountries = List.empty,
        noNationalityReason = None))))

    val nationalityModel = nationalityMustache.transformFormStepToMustacheData(
      partiallyFilledApplicationForm, NationalityController.post, Some(AddressController.get))

    nationalityModel.question.title should be("What is your nationality?")
    nationalityModel.question.postUrl should be("/register-to-vote/forces/nationality")
    nationalityModel.question.backUrl should be("/register-to-vote/forces/address")

    nationalityModel.britishOption.attributes should be("checked=\"checked\"")
    nationalityModel.irishOption.attributes should be("")
    nationalityModel.hasOtherCountryOption.attributes should be("")
    nationalityModel.otherCountriesHead.value should be("")
    nationalityModel.otherCountriesTail.isEmpty should be(true)
    nationalityModel.moreThanOneOtherCountry should be(false)
    nationalityModel.noNationalityReason.value should be("")
  }

  it should "progress form with irish option should produce Mustache Model with values present" in {
    val partiallyFilledApplicationForm = nationalityForm.fill(InprogressForces(
      nationality = Some(PartialNationality(
        british = None,
        irish = Some(true),
        hasOtherCountry = None,
        otherCountries = List.empty,
        noNationalityReason = None))))

    val nationalityModel = nationalityMustache.transformFormStepToMustacheData(
      partiallyFilledApplicationForm, NationalityController.post, Some(AddressController.get))

    nationalityModel.question.title should be("What is your nationality?")
    nationalityModel.question.postUrl should be("/register-to-vote/forces/nationality")
    nationalityModel.question.backUrl should be("/register-to-vote/forces/address")

    nationalityModel.britishOption.attributes should be("")
    nationalityModel.irishOption.attributes should be("checked=\"checked\"")
    nationalityModel.hasOtherCountryOption.attributes should be("")
    nationalityModel.otherCountriesHead.value should be("")
    nationalityModel.otherCountriesTail.isEmpty should be(true)
    nationalityModel.moreThanOneOtherCountry should be(false)
    nationalityModel.noNationalityReason.value should be("")
  }

  it should "progress form with other countries option should produce Mustache Model with values present" in {
    val partiallyFilledApplicationForm = nationalityForm.fill(InprogressForces(
      nationality = Some(PartialNationality(
        british = None,
        irish = None,
        hasOtherCountry = Some(true),
        otherCountries = List("Spain", "France"),
        noNationalityReason = None))))

    val nationalityModel = nationalityMustache.transformFormStepToMustacheData(
      partiallyFilledApplicationForm, NationalityController.post, Some(AddressController.get))

    nationalityModel.question.title should be("What is your nationality?")
    nationalityModel.question.postUrl should be("/register-to-vote/forces/nationality")
    nationalityModel.question.backUrl should be("/register-to-vote/forces/address")

    nationalityModel.britishOption.attributes should be("")
    nationalityModel.irishOption.attributes should be("")
    nationalityModel.hasOtherCountryOption.attributes should be("checked=\"checked\"")
    nationalityModel.otherCountriesHead.value should be("Spain")
    nationalityModel.otherCountriesTail.size should be(1)
    nationalityModel.otherCountriesTail(0).countryName should be("France")
    nationalityModel.moreThanOneOtherCountry should be(true)
    nationalityModel.noNationalityReason.value should be("")
  }

  it should "progress form with other countries and british option should produce Mustache Model with values present" in {
    val partiallyFilledApplicationForm = nationalityForm.fill(InprogressForces(
      nationality = Some(PartialNationality(
        british = Some(true),
        irish = None,
        hasOtherCountry = Some(true),
        otherCountries = List("Spain", "France"),
        noNationalityReason = None))))

    val nationalityModel = nationalityMustache.transformFormStepToMustacheData(
      partiallyFilledApplicationForm, NationalityController.post, Some(AddressController.get))

    nationalityModel.question.title should be("What is your nationality?")
    nationalityModel.question.postUrl should be("/register-to-vote/forces/nationality")
    nationalityModel.question.backUrl should be("/register-to-vote/forces/address")

    nationalityModel.britishOption.attributes should be("checked=\"checked\"")
    nationalityModel.irishOption.attributes should be("")
    nationalityModel.hasOtherCountryOption.attributes should be("checked=\"checked\"")
    nationalityModel.otherCountriesHead.value should be("Spain")
    nationalityModel.otherCountriesTail.size should be(1)
    nationalityModel.otherCountriesTail(0).countryName should be("France")
    nationalityModel.moreThanOneOtherCountry should be(true)
    nationalityModel.noNationalityReason.value should be("")
  }

  it should "progress form with validation errors should produce Model with error list present" in {
    val partiallyFilledApplicationForm = nationalityForm.fillAndValidate(InprogressForces(
      nationality = Some(PartialNationality(
        british = None,
        irish = None,
        hasOtherCountry = None,
        otherCountries = List.empty,
        noNationalityReason = None))))

    val nationalityModel = nationalityMustache.transformFormStepToMustacheData(
      partiallyFilledApplicationForm, NationalityController.post, Some(AddressController.get))

    nationalityModel.question.title should be("What is your nationality?")
    nationalityModel.question.postUrl should be("/register-to-vote/forces/nationality")
    nationalityModel.question.backUrl should be("/register-to-vote/forces/address")

    nationalityModel.britishOption.attributes should be("")
    nationalityModel.irishOption.attributes should be("")
    nationalityModel.hasOtherCountryOption.attributes should be("")
    nationalityModel.otherCountriesHead.value should be("")
    nationalityModel.otherCountriesTail.isEmpty should be(true)
    nationalityModel.moreThanOneOtherCountry should be(false)
    nationalityModel.noNationalityReason.value should be("")

    nationalityModel.question.errorMessages.mkString(", ") should be("Please answer this question")
  }

  it should "progress form with excuse should produce Mustache Model with values present" in {
    val partiallyFilledApplicationForm = nationalityForm.fill(InprogressForces(
      nationality = Some(PartialNationality(
        british = None,
        irish = None,
        hasOtherCountry = None,
        otherCountries = List.empty,
        noNationalityReason = Some("no nationality fake excuse")))))

    val nationalityModel = nationalityMustache.transformFormStepToMustacheData(
      partiallyFilledApplicationForm, NationalityController.post, Some(AddressController.get))

    nationalityModel.question.title should be("What is your nationality?")
    nationalityModel.question.postUrl should be("/register-to-vote/forces/nationality")
    nationalityModel.question.backUrl should be("/register-to-vote/forces/address")

    nationalityModel.britishOption.attributes should be("")
    nationalityModel.irishOption.attributes should be("")
    nationalityModel.hasOtherCountryOption.attributes should be("")
    nationalityModel.otherCountriesHead.value should be("")
    nationalityModel.otherCountriesTail.isEmpty should be(true)
    nationalityModel.moreThanOneOtherCountry should be(false)
    nationalityModel.noNationalityReason.value should be("no nationality fake excuse")
  }
}
