package uk.gov.gds.ier.transaction.crown.applicationFormVote

import play.api.libs.json.{JsNull, Json}
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.WaysToVoteType

class CrownPostalOrProxyFormTests
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
        "postalOrProxyVote.voteType" -> "by-post"
      )
    )
    postalOrProxyVoteForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.postalOrProxyVote.isDefined should be(true)
        val Some(postalOrProxy) = success.postalOrProxyVote
        postalOrProxy.typeVote should be(WaysToVoteType.ByPost)
        postalOrProxy.postalVoteOption should be(Some(true))

        postalOrProxy.deliveryMethod.isDefined should be(true)
        val Some(deliveryMethod) = postalOrProxy.deliveryMethod
        deliveryMethod.deliveryMethod should be(Some("post"))
        deliveryMethod.emailAddress should be(None)
      }
    )
  }

  it should "bind successfully on postal vote true and delivery method email (including email)" in {
    val js = Json.toJson(
      Map(
        "postalOrProxyVote.optIn" -> "true",
        "postalOrProxyVote.deliveryMethod.methodName" -> "email",
        "postalOrProxyVote.deliveryMethod.emailAddress" -> "test@mail.com",
        "postalOrProxyVote.voteType" -> "by-post"
      )
    )
    postalOrProxyVoteForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.postalOrProxyVote.isDefined should be(true)
        val Some(postalOrProxy) = success.postalOrProxyVote
        postalOrProxy.typeVote should be(WaysToVoteType.ByPost)
        postalOrProxy.postalVoteOption should be(Some(true))

        postalOrProxy.deliveryMethod.isDefined should be(true)
        val Some(deliveryMethod) = postalOrProxy.deliveryMethod
        deliveryMethod.deliveryMethod should be(Some("email"))
        deliveryMethod.emailAddress should be(Some("test@mail.com"))
      }
    )
  }

  it should "error out on postal vote true and delivery method email with invalid email" in {
    val js = Json.toJson(
      Map(
        "postalOrProxyVote.optIn" -> "true",
        "postalOrProxyVote.deliveryMethod.methodName" -> "email",
        "postalOrProxyVote.deliveryMethod.emailAddress" -> "emailAddress",
        "postalOrProxyVote.voteType" -> "by-post"
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
        "postalOrProxyVote.voteType" -> "by-post"
      )
    )
    postalOrProxyVoteForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.postalOrProxyVote.isDefined should be(true)
        val Some(postalOrProxy) = success.postalOrProxyVote
        postalOrProxy.typeVote should be(WaysToVoteType.ByPost)
        postalOrProxy.postalVoteOption should be(Some(false))

        postalOrProxy.deliveryMethod.isDefined should be(false)
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    postalOrProxyVoteForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("postalOrProxyVote.optIn") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
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
        "postalOrProxyVote.voteType" -> "by-post"
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
        "postalOrProxyVote.voteType" -> "by-post"
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
