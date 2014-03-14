package uk.gov.gds.ier.validation

import org.scalatest.{Matchers, FlatSpec}
import play.api.data.Forms._
import uk.gov.gds.ier.model.{PartialNationality, InprogressOrdinary}

class InProgressFormTests
  extends FlatSpec
  with Matchers {
  
  behavior of "InProgressForm.confirmationNationalityString"

  def formFor(application: InprogressOrdinary) = {
    InProgressForm(ErrorTransformForm(
      mapping(
        "foo" -> text
      ) (
        foo => application
      ) (
        application => Some("foo")
      )
    ).fill(application))
  }

  it should "handle just irish checked" in {
    val form = formFor(InprogressOrdinary(
      nationality = Some(PartialNationality(
        british = None,
        irish = Some(true),
        hasOtherCountry = None,
        otherCountries = List.empty,
        noNationalityReason = None
      ))
    ))
    form.confirmationNationalityString should be("I am Irish")
  }

  it should "handle just british checked" in {
    val form = formFor(InprogressOrdinary(
      nationality = Some(PartialNationality(
        british = Some(true),
        irish = None,
        hasOtherCountry = None,
        otherCountries = List.empty,
        noNationalityReason = None
      ))
    ))
    form.confirmationNationalityString should be("I am British")
  }

  it should "handle british and irish checked" in {
    val form = formFor(InprogressOrdinary(
      nationality = Some(PartialNationality(
        british = Some(true),
        irish = Some(true),
        hasOtherCountry = None,
        otherCountries = List.empty,
        noNationalityReason = None
      ))
    ))
    form.confirmationNationalityString should be("I am British and Irish")
  }

  it should "handle british, irish and an other nationality checked" in {
    val form = formFor(InprogressOrdinary(
      nationality = Some(PartialNationality(
        british = Some(true),
        irish = Some(true),
        hasOtherCountry = Some(true),
        otherCountries = List("New Zealand"),
        noNationalityReason = None
      ))
    ))
    form.confirmationNationalityString should be(
      "I am British, Irish and a citizen of New Zealand"
    )
  }

  it should "handle british, irish and two other nationalities checked" in {
    val form = formFor(InprogressOrdinary(
      nationality = Some(PartialNationality(
        british = Some(true),
        irish = Some(true),
        hasOtherCountry = Some(true),
        otherCountries = List("New Zealand", "India"),
        noNationalityReason = None
      ))
    ))
    form.confirmationNationalityString should be(
      "I am British, Irish and a citizen of New Zealand and India"
    )
  }

  it should "handle british, irish and three other nationalities checked" in {
    val form = formFor(InprogressOrdinary(
      nationality = Some(PartialNationality(
        british = Some(true),
        irish = Some(true),
        hasOtherCountry = Some(true),
        otherCountries = List("New Zealand", "India", "Japan"),
        noNationalityReason = None
      ))
    ))
    form.confirmationNationalityString should be(
      "I am British, Irish and a citizen of New Zealand, India and Japan"
    )
  }

  it should "handle an other nationality checked" in {
    val form = formFor(InprogressOrdinary(
      nationality = Some(PartialNationality(
        british = None,
        irish = None,
        hasOtherCountry = Some(true),
        otherCountries = List("New Zealand"),
        noNationalityReason = None
      ))
    ))
    form.confirmationNationalityString should be(
      "I am a citizen of New Zealand"
    )
  }

  it should "handle an three other nationalities checked" in {
    val form = formFor(InprogressOrdinary(
      nationality = Some(PartialNationality(
        british = None,
        irish = None,
        hasOtherCountry = Some(true),
        otherCountries = List("New Zealand", "India", "Japan"),
        noNationalityReason = None
      ))
    ))
    form.confirmationNationalityString should be(
      "I am a citizen of New Zealand, India and Japan"
    )
  }

  it should "handle two other nationalities checked" in {
    val form = formFor(InprogressOrdinary(
      nationality = Some(PartialNationality(
        british = None,
        irish = None,
        hasOtherCountry = Some(true),
        otherCountries = List("New Zealand", "India"),
        noNationalityReason = None
      ))
    ))
    form.confirmationNationalityString should be(
      "I am a citizen of New Zealand and India"
    )
  }
}
