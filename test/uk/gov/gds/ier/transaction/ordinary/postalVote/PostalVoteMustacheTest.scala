package uk.gov.gds.ier.transaction.ordinary.postalVote

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.{WithMockRemoteAssets, TestHelpers}
import scala.Some
import controllers.step.ordinary.routes._
import controllers.step.routes._
import uk.gov.gds.ier.model.{PostalVote}
import uk.gov.gds.ier.model.PostalVoteDeliveryMethod
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

class PostalVoteMustacheTest
  extends FlatSpec
  with Matchers
  with PostalVoteForms
  with ErrorMessages
  with FormKeys
  with TestHelpers
  with WithMockRemoteAssets
  with PostalVoteMustache {

  it should "empty progress form should produce empty Model" in runningApp {
    val emptyApplicationForm = postalVoteForm
    val postalVoteModel = mustache.data(
      emptyApplicationForm,
      PostalVoteController.post,
      InprogressOrdinary()
    ).asInstanceOf[PostalVoteModel]

    postalVoteModel.question.title should be("Do you want to apply for a postal vote?")
    postalVoteModel.question.postUrl should be("/register-to-vote/postal-vote")

    postalVoteModel.postCheckboxYes.attributes should be("")
    postalVoteModel.postCheckboxNo.attributes should be("")
    postalVoteModel.deliveryByEmail.attributes should be("")
    postalVoteModel.deliveryByPost.attributes should be("")
    postalVoteModel.emailField.value should be("")

  }

  it should "progress form with no for postal vote" in runningApp {
    val partiallyFilledApplicationForm = postalVoteForm.fill(InprogressOrdinary(
      postalVote = Some(PostalVote(
        postalVoteOption = Some(false),
        deliveryMethod = None))))

    val postalVoteModel = mustache.data(
      partiallyFilledApplicationForm,
      PostalVoteController.post,
      InprogressOrdinary()
    ).asInstanceOf[PostalVoteModel]

    postalVoteModel.question.title should be("Do you want to apply for a postal vote?")
    postalVoteModel.question.postUrl should be("/register-to-vote/postal-vote")

    postalVoteModel.postCheckboxYes.attributes should be("")
    postalVoteModel.postCheckboxNo.attributes should be("checked=\"checked\"")
    postalVoteModel.deliveryByEmail.attributes should be("")
    postalVoteModel.deliveryByPost.attributes should be("")
    postalVoteModel.emailField.value should be("")
  }

  it should "progress form with yes for postal vote and post for delivery method " +
    "should produce form with values" in runningApp {
    val partiallyFilledApplicationForm = postalVoteForm.fill(InprogressOrdinary(
      postalVote = Some(PostalVote(
        postalVoteOption = Some(true),
        deliveryMethod = Some(PostalVoteDeliveryMethod(deliveryMethod = Some("post"), None))))))

    val postalVoteModel = mustache.data(
      partiallyFilledApplicationForm,
      PostalVoteController.post,
      InprogressOrdinary()
    ).asInstanceOf[PostalVoteModel]

    postalVoteModel.question.title should be("Do you want to apply for a postal vote?")
    postalVoteModel.question.postUrl should be("/register-to-vote/postal-vote")

    postalVoteModel.postCheckboxYes.attributes should be("checked=\"checked\"")
    postalVoteModel.postCheckboxNo.attributes should be("")
    postalVoteModel.deliveryByEmail.attributes should be("")
    postalVoteModel.deliveryByPost.attributes should be("checked=\"checked\"")
    postalVoteModel.emailField.value should be("")
  }

  it should "progress form with yes for postal vote and email for delivery method " +
    "should produce form with values" in runningApp {
    val partiallyFilledApplicationForm = postalVoteForm.fill(InprogressOrdinary(
      postalVote = Some(PostalVote(
        postalVoteOption = Some(true),
        deliveryMethod = Some(PostalVoteDeliveryMethod(deliveryMethod = Some("email"),
            emailAddress = Some("test@test.com")))))))

    val postalVoteModel = mustache.data(
      partiallyFilledApplicationForm,
      PostalVoteController.post,
      InprogressOrdinary()
    ).asInstanceOf[PostalVoteModel]

    postalVoteModel.question.title should be("Do you want to apply for a postal vote?")
    postalVoteModel.question.postUrl should be("/register-to-vote/postal-vote")

    postalVoteModel.postCheckboxYes.attributes should be("checked=\"checked\"")
    postalVoteModel.postCheckboxNo.attributes should be("")
    postalVoteModel.deliveryByEmail.attributes should be("checked=\"checked\"")
    postalVoteModel.deliveryByPost.attributes should be("")
    postalVoteModel.emailField.value should be("test@test.com")
  }
}
