package uk.gov.gds.ier.transaction.ordinary.postalVote

import play.api.libs.json.{JsNull, Json}
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{PostalVoteDeliveryMethod, PostalVote}

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
        "postalVote.deliveryMethod.methodName" -> "post"
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.postalVote should be(Some(PostalVote(Some(true),Some(PostalVoteDeliveryMethod(Some("post"),None)))))
      }
    )
  }

  it should "bind successfully on postal vote true and delivery method email (including email)" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> "true",
        "postalVote.deliveryMethod.methodName" -> "email",
        "postalVote.deliveryMethod.emailAddress" -> "test@mail.com"
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.postalVote should be(Some(PostalVote(Some(true),Some(PostalVoteDeliveryMethod(Some("email"),Some("test@mail.com"))))))
      }
    )
  }

  it should "bind successfully on postal vote true and delivery method email (including email with special characters)" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> "true",
        "postalVote.deliveryMethod.methodName" -> "email",
        "postalVote.deliveryMethod.emailAddress" -> "o'fake’._%+’'-@fake._%+’'-.co.uk"
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.postalVote should be(Some(PostalVote(Some(true),Some(PostalVoteDeliveryMethod(Some("email"),Some("o'fake’._%+’'-@fake._%+’'-.co.uk"))))))
      }
    )
  }

  it should "error out on postal vote true and delivery method email with invalid email" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> "true",
        "postalVote.deliveryMethod.methodName" -> "email",
        "postalVote.deliveryMethod.emailAddress" -> "emailAddress"
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("postalVote.deliveryMethod.emailAddress") should be(Seq("ordinary_postalVote_error_enterValidEmail"))
        hasErrors.globalErrorMessages should be(Seq("ordinary_postalVote_error_enterValidEmail"))
      },
      success => fail("Should have thrown an error")
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
        success.postalVote should be(Some(PostalVote(Some(false),None)))
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    postalVoteForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("postalVote.optIn") should be(Seq("ordinary_postalVote_error_answerThis"))
        hasErrors.globalErrorMessages should be(Seq("ordinary_postalVote_error_answerThis"))
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
        hasErrors.errorMessages("postalVote.optIn") should be(Seq("ordinary_postalVote_error_answerThis"))
        hasErrors.globalErrorMessages should be(Seq("ordinary_postalVote_error_answerThis"))
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
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "postalVote.deliveryMethod.methodName" -> Seq("ordinary_postalVote_error_answerThis")
        ))
        hasErrors.globalErrorMessages should be(Seq("ordinary_postalVote_error_answerThis"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out on postalVote.optIn true, method delivery email and empty email address" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> "true",
        "postalVote.deliveryMethod.methodName" -> "email"
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "postalVote.deliveryMethod.emailAddress" -> Seq("ordinary_postalVote_error_enterYourEmail")
        ))
        hasErrors.globalErrorMessages should be(Seq("ordinary_postalVote_error_enterYourEmail"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "ignore a malformed email if other deliveryMethod is chosen" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> "true",
        "postalVote.deliveryMethod.methodName" -> "post",
        "postalVote.deliveryMethod.emailAddress" -> "malformedEmail"
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(", ")),
      success => {
        val Some(postalVote) = success.postalVote

        postalVote.postalVoteOption should be(Some(true))
        val Some(deliveryMethod) = postalVote.deliveryMethod
        deliveryMethod should have(
          'deliveryMethod (Some("post")),
          'emailAddress (None)
        )
      }
    )
  }

  it should "ignore email/post option if optIn is false" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> "false",
        "postalVote.deliveryMethod.methodName" -> "post",
        "postalVote.deliveryMethod.emailAddress" -> "malformedEmail"
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(", ")),
      success => {
        val Some(postalVote) = success.postalVote

        postalVote.postalVoteOption should be(Some(false))
        postalVote.deliveryMethod should be(None)
      }
    )
  }
}
