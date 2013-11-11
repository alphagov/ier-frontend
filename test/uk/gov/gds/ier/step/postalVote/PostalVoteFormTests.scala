package uk.gov.gds.ier.step.postalVote
import play.api.libs.json.{JsNull, Json}
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}

@RunWith(classOf[JUnitRunner])
class PostalVoteFormTests 
  extends FlatSpec
  with Matchers
  with PostalVoteForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  it should "bind successfully (true)" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> "true"
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.postalVoteOptin should be(Some(true))
      }
    )
  }

  it should "bind successfully (false)" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> "false"
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.postalVoteOptin should be(Some(false))
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    postalVoteForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorMessages("postalVote") should be(Seq("Please answer this question"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out on empty values" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> ""
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorMessages("postalVote") should be(Seq("Please answer this question"))
      },
      success => fail("Should have thrown an error")
    )
  }
}
