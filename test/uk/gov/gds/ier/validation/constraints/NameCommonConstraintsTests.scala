package uk.gov.gds.ier.validation.constraints

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.{PreviousName, Name}
import play.api.data.validation.{Invalid, Valid}

class NameCommonConstraintsTests
  extends FlatSpec
  with Matchers 
  with ErrorMessages
  with FormKeys
  with TestHelpers 
  with NameCommonConstraints {
  
  val serialiser = jsonSerialiser

  behavior of "NameCommonConstraints.firstNameTooLong"

  it should "be valid for a short name" in {
    firstNameNotTooLong.apply(Some(Name("NameNotTooLong", None, "Smith"))) should be(Valid)
  }

  it should "be invalid for a long name" in {
    firstNameNotTooLong.apply(Some(Name("NameIsWayTooLongNameIsWayTooLong" +
      "NameIsWayTooLongNameIsWayTooLongNameIsWayTooLongNameIsWayTooLong" +
      "NameIsWayTooLongNameIsWayTooLongNameIsWayTooLongNameIsWayTooLong" +
      "NameIsWayTooLongNameIsWayTooLongNameIsWayTooLongNameIsWayTooLong" +
      "NameIsWayTooLongNameIsWayTooLongNameIsWayTooLongNameIsWayTooLong",
      None, "Smith"))) should be(Invalid(firstNameMaxLengthError, keys.name.firstName))
  }

  behavior of "NameCommonConstraints.lastNameTooLong"

  it should "be valid for a short name" in {
    lastNameNotTooLong.apply(Some(Name("John", None, "NameNotTooLong"))) should be(Valid)
  }

  it should "be invalid for a long name" in {
    lastNameNotTooLong.apply(Some(Name("John", None, "NameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLong"))) should be(Invalid(lastNameMaxLengthError, keys.name.lastName))
  }

  behavior of "NameCommonConstraints.middleNamesTooLong"

  it should "be valid for a short name" in {
    middleNamesNotTooLong.apply(Some(Name("John", Some("NameNotTooLong"), "Smith"))) should be(Valid)
  }

  it should "be valid for a no middleName" in {
    middleNamesNotTooLong.apply(Some(Name("John", None, "Smith"))) should be(Valid)
  }

  it should "be invalid for a long name" in {
    middleNamesNotTooLong.apply(Some(Name("John", Some("NameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLong"), "Smith"))) should be(Invalid(middleNameMaxLengthError, keys.name.middleNames))
  }


  behavior of "NameCommonConstraints.prevFirstNameTooLong"

  it should "be valid for a short name" in {
    val samplePreviousName = Some(PreviousName(
      hasPreviousName = true,
      previousName = Some(Name("NameNotTooLong", None, "Smith"))
    ))

    prevFirstNameNotTooLong.apply(samplePreviousName) should be(Valid)
  }

  it should "be invalid for a long name" in {
    val samplePreviousName = Some(PreviousName(
      hasPreviousName = true,
      previousName = Some(Name("NameIsWayTooLongNameIsWayTooLong" +
        "NameIsWayTooLongNameIsWayTooLongNameIsWayTooLongNameIsWayTooLong" +
        "NameIsWayTooLongNameIsWayTooLongNameIsWayTooLongNameIsWayTooLong" +
        "NameIsWayTooLongNameIsWayTooLongNameIsWayTooLongNameIsWayTooLong" +
        "NameIsWayTooLongNameIsWayTooLongNameIsWayTooLongNameIsWayTooLong",
        None, "Smith"))
    ))

    prevFirstNameNotTooLong.apply(samplePreviousName) should be(Invalid(
      previousFirstNameMaxLengthError,
      keys.previousName.previousName.firstName))
  }

  behavior of "NameCommonConstraints.prevLastNameTooLong"

  it should "be valid for a short name" in {
    val samplePreviousName = Some(PreviousName(
      hasPreviousName = true,
      previousName = Some(Name("John", None, "NameNotTooLong"))
    ))

    prevLastNameNotTooLong.apply(samplePreviousName) should be(Valid)
  }

  it should "be invalid for a long name" in {
    val samplePreviousName = Some(PreviousName(
      hasPreviousName = true,
      previousName = Some(Name("John", None, "NameNotTooLongNameNotTooLong" +
        "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
        "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
        "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
        "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
        "NameNotTooLong"))
    ))

    prevLastNameNotTooLong.apply(samplePreviousName) should be(Invalid(
      previousLastNameMaxLengthError,
      keys.previousName.previousName.lastName))
  }

  behavior of "NameCommonConstraints.prevMiddleNamesTooLong"

  it should "be valid for a short name" in {
    val samplePreviousName = Some(PreviousName(
      hasPreviousName = true,
      previousName = Some(Name("John", Some("NameNotTooLong"), "Smith"))
    ))
    prevMiddleNamesNotTooLong.apply(samplePreviousName) should be(Valid)
  }

  it should "be valid for a no middleName" in {
    val samplePreviousName = Some(PreviousName(
      hasPreviousName = true,
      previousName = Some(Name("John", None, "Smith"))
    ))

    prevMiddleNamesNotTooLong.apply(samplePreviousName) should be(Valid)
  }

  it should "be invalid for a long name" in {
    val samplePreviousName = Some(PreviousName(
      hasPreviousName = true,
      previousName = Some(Name("John", Some("NameNotTooLongNameNotTooLong" +
        "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
        "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
        "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
        "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
        "NameNotTooLong"), "Smith"))
    ))

    prevMiddleNamesNotTooLong.apply(samplePreviousName) should be(Invalid(
      previousMiddleNameMaxLengthError,
      keys.previousName.previousName.middleNames))
  }

//  behavior of "NameCommonConstraints.prevNameFilledIfHasPrevIsTrue"
//
//  it should "be valid for hasPreviousName = true and filled Previous Name" in {
//    prevNameFilledIfHasPrevIsTrue.apply(
//      PreviousName(
//        hasPreviousName = true,
//        previousName = Some(Name("John", None, "Smith"))
//      )
//    ) should be(Valid)
//  }
//
//  it should "be valid for hasPreviousName = false and no previous name" in {
//    prevNameFilledIfHasPrevIsTrue.apply(
//      PreviousName(
//        hasPreviousName = false,
//        previousName = None
//      )
//    ) should be(Valid)
//  }
//
//  it should "be invalid for hasPreviousName = true and no previousName" in {
//    prevNameFilledIfHasPrevIsTrue.apply(
//      PreviousName(
//        hasPreviousName = true,
//        previousName = None
//      )
//    ) should be(
//      Invalid("Please enter your previous name",
//      keys.previousName.previousName.firstName,
//      keys.previousName.previousName.lastName))
//  }
//
//  it should "be valid for hasPreviousName = false and filled previousName" in {
//    prevNameFilledIfHasPrevIsTrue.apply(
//      PreviousName(
//        hasPreviousName = false,
//        previousName = Some(Name("John", None, "Smith"))
//      )
//    ) should be(Valid)
//  }
}
