package uk.gov.gds.ier.model

import uk.gov.gds.ier.test.{TestHelpers, CustomMatchers}
import org.scalatest.{Matchers, FlatSpec}

class PreviousNameTests
  extends FlatSpec
  with Matchers
  with CustomMatchers
  with TestHelpers {

  behavior of "toApiMap with hasPreviuosName = true"
  it should "generate the expected payload with first,last and middle names" in {
    val sutPreviousName = PreviousName(
      hasPreviousName = true,
      previousName = Some(Name(
        firstName = "John",
        middleNames = Some("James"),
        lastName = "Smith"
      )),
      reason = Some("some reason")
    )

    val expected = Map(
      "pfn" -> "John",
      "pmn" -> "James",
      "pln" -> "Smith",
      "nameChangeReason" -> "some reason"
    )
    sutPreviousName.toApiMap() should matchMap(expected)
  }

  it should "generate the expected payload with first and last names" in {
    val sutPreviousName = PreviousName(
      hasPreviousName = true,
      previousName = Some(Name(
        firstName = "John",
        middleNames = None,
        lastName = "Smith"
      )),
      reason = Some("some reason")
    )

    val expected = Map(
      "pfn" -> "John",
      "pln" -> "Smith",
      "nameChangeReason" -> "some reason"
    )
    sutPreviousName.toApiMap() should matchMap(expected)
  }


  behavior of "toApiMap with hasPreviuosName = false"
  it should "generate the expected empty map" in {
    val sutPreviousName = PreviousName(
      hasPreviousName = false,
      previousName = None
    )

    sutPreviousName.toApiMap() shouldBe empty
  }

  it should "generate the expected empty map even if name and last name are not empty" in {
    val sutPreviousName = PreviousName(
      hasPreviousName = false,
      previousName = Some(Name(
        firstName = "John",
        middleNames = None,
        lastName = "Smith"
      )),
      reason = Some("some reason")
    )

    sutPreviousName.toApiMap() shouldBe empty
  }

}
