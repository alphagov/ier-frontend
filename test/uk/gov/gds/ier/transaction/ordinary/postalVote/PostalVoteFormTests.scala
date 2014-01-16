package uk.gov.gds.ier.transaction.ordinary.postalVote

import play.api.libs.json.{JsNull, Json}
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}

class PostalVoteFormTests 
  extends FlatSpec
  with Matchers
  with PostalVoteForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  it should "bind successfully on postal vote true and delivery method post" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> "true",
        "deliveryMethod.methodName" -> "post"
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.postalVoteOptin should be(Some(true))
      }
    )
  }

  it should "bind successfully on postal vote true and delivery method email (including email)" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> "true",
        "deliveryMethod.methodName" -> "email",
        "deliveryMethod.emailAddress" -> "deliveryMethod.emailAddress"
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.postalVoteOptin should be(Some(true))
      }
    )
  }

  it should "bind successfully on postal vote false" in {
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
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("postalVote.optIn") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
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
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("postalVote.optIn") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out on empty method delivery when postalVote.optIn is true" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> "true"
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out on postalVote.optIn true, method delivery email and empty email address" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> "true",
        "deliveryMethod.methodName" -> "email"
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("deliveryMethod.emailAddress") should be(Seq("Please enter your email address"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your email address"))
      },
      success => fail("Should have thrown an error")
    )
  }
}
