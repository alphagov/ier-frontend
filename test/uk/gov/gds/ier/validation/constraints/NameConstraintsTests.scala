package uk.gov.gds.ier.validation.constraints

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.{PreviousName, Name}
import play.api.data.validation.{Invalid, Valid}

class NameConstraintsTests
  extends FlatSpec
  with Matchers 
  with ErrorMessages
  with FormKeys
  with TestHelpers 
  with NameConstraints {
  
  val serialiser = jsonSerialiser

  behavior of "NameConstraints.firstNameTooLong"

  it should "be valid for a short name" in {
    firstNameNotTooLong.apply(Name("NameNotTooLong", None, "Smith")) should be(Valid)
  }

  it should "be invalid for a long name" in {
    firstNameNotTooLong.apply(Name("NameIsWayTooLongNameIsWayTooLong" +
      "NameIsWayTooLongNameIsWayTooLongNameIsWayTooLongNameIsWayTooLong" +
      "NameIsWayTooLongNameIsWayTooLongNameIsWayTooLongNameIsWayTooLong" +
      "NameIsWayTooLongNameIsWayTooLongNameIsWayTooLongNameIsWayTooLong" +
      "NameIsWayTooLongNameIsWayTooLongNameIsWayTooLongNameIsWayTooLong",
      None, "Smith")) should be(Invalid(firstNameMaxLengthError, keys.name.firstName))
  }

  behavior of "NameConstraints.lastNameTooLong"

  it should "be valid for a short name" in {
    lastNameNotTooLong.apply(Name("John", None, "NameNotTooLong")) should be(Valid)
  }

  it should "be invalid for a long name" in {
    lastNameNotTooLong.apply(Name("John", None, "NameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLong")) should be(Invalid(lastNameMaxLengthError, keys.name.lastName))
  }

  behavior of "NameConstraints.middleNamesTooLong"

  it should "be valid for a short name" in {
    middleNamesNotTooLong.apply(Name("John", Some("NameNotTooLong"), "Smith")) should be(Valid)
  }

  it should "be valid for a no middleName" in {
    middleNamesNotTooLong.apply(Name("John", None, "Smith")) should be(Valid)
  }

  it should "be invalid for a long name" in {
    middleNamesNotTooLong.apply(Name("John", Some("NameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLong"), "Smith")) should be(Invalid(middleNameMaxLengthError, keys.name.middleNames))
  }


  behavior of "NameConstraints.prevFirstNameTooLong"

  it should "be valid for a short name" in {
    prevFirstNameNotTooLong.apply(Name("NameNotTooLong", None, "Smith")) should be(Valid)
  }

  it should "be invalid for a long name" in {
    prevFirstNameNotTooLong.apply(Name("NameIsWayTooLongNameIsWayTooLong" +
      "NameIsWayTooLongNameIsWayTooLongNameIsWayTooLongNameIsWayTooLong" +
      "NameIsWayTooLongNameIsWayTooLongNameIsWayTooLongNameIsWayTooLong" +
      "NameIsWayTooLongNameIsWayTooLongNameIsWayTooLongNameIsWayTooLong" +
      "NameIsWayTooLongNameIsWayTooLongNameIsWayTooLongNameIsWayTooLong",
      None, "Smith")) should be(Invalid(firstNameMaxLengthError,
        keys.previousName.previousName.firstName))
  }

  behavior of "NameConstraints.prevLastNameTooLong"

  it should "be valid for a short name" in {
    prevLastNameNotTooLong.apply(Name("John", None, "NameNotTooLong")) should be(Valid)
  }

  it should "be invalid for a long name" in {
    prevLastNameNotTooLong.apply(Name("John", None, "NameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLong")) should be(Invalid(lastNameMaxLengthError,
        keys.previousName.previousName.lastName))
  }

  behavior of "NameConstraints.prevMiddleNamesTooLong"

  it should "be valid for a short name" in {
    prevMiddleNamesNotTooLong.apply(Name("John", Some("NameNotTooLong"), "Smith")) should be(Valid)
  }

  it should "be valid for a no middleName" in {
    prevMiddleNamesNotTooLong.apply(Name("John", None, "Smith")) should be(Valid)
  }

  it should "be invalid for a long name" in {
    prevMiddleNamesNotTooLong.apply(Name("John", Some("NameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLong"), "Smith")) should be(Invalid(middleNameMaxLengthError,
        keys.previousName.previousName.middleNames))
  }

  behavior of "NameConstraints.prevNameFilledIfHasPrevIsTrue"

  it should "be valid for hasPreviousName = true and filled Previous Name" in {
    prevNameFilledIfHasPrevIsTrue.apply(
      PreviousName(
        hasPreviousName = true,
        previousName = Some(Name("John", None, "Smith"))
      )
    ) should be(Valid)
  }

  it should "be valid for hasPreviousName = false and no previous name" in {
    prevNameFilledIfHasPrevIsTrue.apply(
      PreviousName(
        hasPreviousName = false,
        previousName = None
      )
    ) should be(Valid)
  }

  it should "be invalid for hasPreviousName = true and no previousName" in {
    prevNameFilledIfHasPrevIsTrue.apply(
      PreviousName(
        hasPreviousName = true,
        previousName = None
      )
    ) should be(
      Invalid("Please enter your previous name",
      keys.previousName.previousName.firstName,
      keys.previousName.previousName.lastName))
  }

  it should "be valid for hasPreviousName = false and filled previousName" in {
    prevNameFilledIfHasPrevIsTrue.apply(
      PreviousName(
        hasPreviousName = false,
        previousName = Some(Name("John", None, "Smith"))
      )
    ) should be(Valid)
  }
}
