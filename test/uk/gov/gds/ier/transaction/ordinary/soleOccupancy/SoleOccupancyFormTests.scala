package uk.gov.gds.ier.transaction.ordinary.soleOccupancy

import uk.gov.gds.ier.model.SoleOccupancyOption
import uk.gov.gds.ier.test.{FormTestSuite, WithMockScotlandService}

class SoleOccupancyFormTests
  extends FormTestSuite
  with SoleOccupancyForms
  with WithMockScotlandService {

  it should "bind successfully on sole occupancy yes" in {
    val js = Json.toJson(
      Map(
        "soleOccupancy.optIn" -> "yes"
      )
    )

    soleOccupancyForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.soleOccupancy should be(Some(SoleOccupancyOption(true, "yes")))
      }
    )
  }

  it should "bind successfully on sole occupancy no" in {
    val js = Json.toJson(
      Map(
        "soleOccupancy.optIn" -> "no"
      )
    )

    soleOccupancyForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.soleOccupancy should be(Some(SoleOccupancyOption(false, "no")))
      }
    )
  }

  it should "bind successfully on sole occupancy not sure" in {
    val js = Json.toJson(
      Map(
        "soleOccupancy.optIn" -> "not-sure"
      )
    )

    soleOccupancyForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.soleOccupancy should be(Some(SoleOccupancyOption(false, "not-sure")))
      }
    )
  }

  it should "bind successfully on sole occupancy prefer not to say" in {
    val js = Json.toJson(
      Map(
        "soleOccupancy.optIn" -> "skip-this-question"
      )
    )

    soleOccupancyForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.soleOccupancy should be(Some(SoleOccupancyOption(false, "skip-this-question")))
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    soleOccupancyForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("soleOccupancy.optIn") should be(Seq("ordinary_soleOccupancy_error_answerThis"))
        hasErrors.globalErrorMessages should be(Seq("ordinary_soleOccupancy_error_answerThis"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out on empty values" in {
    val js = Json.toJson(
      Map(
        "soleOccupancy.optIn" -> ""
      )
    )
    soleOccupancyForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("soleOccupancy.optIn") should be(Seq("ordinary_soleOccupancy_error_answerThis"))
        hasErrors.globalErrorMessages should be(Seq("ordinary_soleOccupancy_error_answerThis"))
      },
      success => fail("Should have thrown an error")
    )
  }
}
