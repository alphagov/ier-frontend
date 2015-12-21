package uk.gov.gds.ier.transaction.overseas.previousName

import uk.gov.gds.ier.test.FormTestSuite

class OverseasPreviousNameFormTests
  extends FormTestSuite
  with PreviousNameForms {

  it should "accept an empty json" in {
    val js = JsNull
    previousNameForm.bind(js).fold(
      hasErrors => {
      fail(serialiser.toJson(hasErrors.prettyPrint))
      },
      success =>
        success.previousName.isDefined should be(false)
    )
  }

  it should "check for too long names" in {
    val inputDataJson = Json.toJson(
      Map(
        "previousName.hasPreviousName" -> "true",
        "previousName.hasPreviousNameOption" -> "true",
        "previousName.previousName.firstName" -> textTooLong,
        "previousName.previousName.middleNames" -> textTooLong,
        "previousName.previousName.lastName" -> textTooLong
      )
    )
    previousNameForm.bind(inputDataJson).fold(
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "previousName.previousName.firstName" -> Seq("Previous first name can be no longer than 35 characters"),
          "previousName.previousName.middleNames" -> Seq("Previous middle names can be no longer than 100 characters"),
          "previousName.previousName.lastName" -> Seq("Previous last name can be no longer than 35 characters")
        ))
      },
      success => fail("Should have errored out")
    )
  }

  it should "successfully bind with no previous name" in {
    val js = Json.toJson(
      Map(
        "previousName.hasPreviousName" -> "false",
        "previousName.hasPreviousNameOption" -> "false",
        "previousName.previousName.firstName" -> "Jonny",
        "previousName.previousName.middleNames" -> "Joe",
        "previousName.previousName.lastName" -> "Bloggs",
        "previousName.reason" -> "because I can"
      )
    )
    previousNameForm.bind(js).fold(
      hasErrors => {
        fail(serialiser.toJson(hasErrors.prettyPrint))
      },
      success => {
        success.previousName.isDefined should be(true)
        val previousName = success.previousName.get
        previousName.previousName.isDefined should be(false)
        previousName.hasPreviousName should be(false)
        previousName.hasPreviousNameOption should be("false")
      }
    )
  }
  it should "successfully bind" in {
    val js = Json.toJson(
      Map(
        "previousName.hasPreviousName" -> "true",
        "previousName.hasPreviousNameOption" -> "true",
        "previousName.previousName.firstName" -> "Jonny",
        "previousName.previousName.middleNames" -> "Joe",
        "previousName.previousName.lastName" -> "Bloggs",
        "previousName.reason" -> "because I can"
      )
    )
    previousNameForm.bind(js).fold(
      hasErrors => {
        fail(serialiser.toJson(hasErrors.prettyPrint))
      },
      success => {
        success.previousName.isDefined should be(true)
        success.previousName match {
          case Some(previousName) => {
            previousName.hasPreviousName should be(true)
            previousName.hasPreviousNameOption should be("true")
            previousName.reason.isDefined should be(true)
            previousName.reason.get should be("because I can")

            previousName.previousName match {
              case Some(prevName) => {
                prevName.firstName should be("Jonny")
                prevName.middleNames should be(Some("Joe"))
                prevName.lastName should be("Bloggs")
                prevName.lastName should be("Bloggs")
              }
              case _ => fail("previous name data not mapped")
            }

          }
          case _ => fail("previous name not mapped")
        }
      }


    )
  }

  it should "ignore invalid input if previousName = false" in {
    val js = Map(
      "previousName.hasPreviousName" -> "false",
      "previousName.hasPreviousNameOption" -> "false",
      "previousName.previousName.firstName" -> "Jonny",
      "previousName.previousName.middleNames" -> "Joe",
      "previousName.previousName.lastName" -> "Bloggs",
      "previousName.reason" -> "because I can"
    )
    previousNameForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(", ")),
      success => {
        val Some(previousName) = success.previousName
        previousName should have(
          'hasPreviousName (false),
          'hasPreviousNameOption ("false"),
          'previousName (None)
        )
      }
    )
  }
}

