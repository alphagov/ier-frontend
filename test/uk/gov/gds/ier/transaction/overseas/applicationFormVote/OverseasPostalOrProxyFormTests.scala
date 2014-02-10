package uk.gov.gds.ier.transaction.overseas.applicationFormVote

import play.api.libs.json.{JsNull, Json}
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{PostalOrProxyVote, PostalVoteDeliveryMethod, PostalVote}

class OverseasPostalOrProxyFormTests
  extends FlatSpec
  with Matchers
  with PostalOrProxyVoteForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  it should "bind successfully on postal vote true and delivery method post" in {
    val js = Json.toJson(
      Map(
        "postalOrProxyVote.optIn" -> "true",
        "postalOrProxyVote.deliveryMethod.methodName" -> "post",
        "postalOrProxyVote.voteType" -> "postal"
      )
    )
    postalOrProxyVoteForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.postalOrProxyVote should be(Some(PostalOrProxyVote("postal", Some(true),Some(PostalVoteDeliveryMethod(Some("post"),None)))))
      }
    )
  }

  it should "bind successfully on postal vote true and delivery method email (including email)" in {
    val js = Json.toJson(
      Map(
        "postalOrProxyVote.optIn" -> "true",
        "postalOrProxyVote.deliveryMethod.methodName" -> "email",
        "postalOrProxyVote.deliveryMethod.emailAddress" -> "test@mail.com",
        "postalOrProxyVote.voteType" -> "postal"
      )
    )
    postalOrProxyVoteForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.postalOrProxyVote should be(Some(PostalOrProxyVote("postal", Some(true),Some(PostalVoteDeliveryMethod(Some("email"),Some("test@mail.com"))))))
      }
    )
  }

  it should "error out on postal vote true and delivery method email with invalid email" in {
    val js = Json.toJson(
      Map(
        "postalOrProxyVote.optIn" -> "true",
        "postalOrProxyVote.deliveryMethod.methodName" -> "email",
        "postalOrProxyVote.deliveryMethod.emailAddress" -> "emailAddress",
        "postalOrProxyVote.voteType" -> "postal"
      )
    )
    postalOrProxyVoteForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("postalOrProxyVote.deliveryMethod.emailAddress") should be(Seq("Please enter a valid email address"))
        hasErrors.globalErrorMessages should be(Seq("Please enter a valid email address"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "bind successfully on postal vote false" in {
    val js = Json.toJson(
      Map(
        "postalOrProxyVote.optIn" -> "false",
        "postalOrProxyVote.voteType" -> "postal"
      )
    )
    postalOrProxyVoteForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.postalOrProxyVote should be(Some(PostalOrProxyVote("postal", Some(false),None)))
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    postalOrProxyVoteForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(4)
        hasErrors.errorMessages("postalOrProxyVote.optIn") should be(Seq("Please answer this question"))
        hasErrors.errorMessages("postalOrProxyVote.voteType") should be(Seq("error.required"))
        hasErrors.globalErrorMessages should be(Seq("error.required", "Please answer this question"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out on empty values" in {
    val js = Json.toJson(
      Map(
        "postalOrProxyVote.optIn" -> "",
        "postalOrProxyVote.voteType" -> ""
      )
    )
    postalOrProxyVoteForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("postalOrProxyVote.optIn") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out on empty method delivery when postalVote.optIn is true" in {
    val js = Json.toJson(
      Map(
        "postalOrProxyVote.optIn" -> "true",
        "postalOrProxyVote.voteType" -> "postal"
      )
    )
    postalOrProxyVoteForm.bind(js).fold(
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
        "postalOrProxyVote.optIn" -> "true",
        "postalOrProxyVote.deliveryMethod.methodName" -> "email",
        "postalOrProxyVote.voteType" -> "postal"
      )
    )
    postalOrProxyVoteForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("") should be(Seq("Please enter your email address"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your email address"))
      },
      success => fail("Should have thrown an error")
    )
  }
}
