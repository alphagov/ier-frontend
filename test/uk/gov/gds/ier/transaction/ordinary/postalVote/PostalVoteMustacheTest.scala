package uk.gov.gds.ier.transaction.ordinary.postalVote

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import scala.Some
import controllers.step.ordinary.routes._
import controllers.step.routes._
import uk.gov.gds.ier.model.{PostalVote, InprogressOrdinary}
import uk.gov.gds.ier.model.PostalVoteDeliveryMethod

class PostalVoteMustacheTest
  extends FlatSpec
  with Matchers
  with PostalVoteForms
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val postalVoteMustache = new PostalVoteMustache {}

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = postalVoteForm
    val postalVoteModel = postalVoteMustache.transformFormStepToMustacheData(
      emptyApplicationForm, PostalVoteController.post, Some(ContactController.get))

    postalVoteModel.question.title should be("Do you want to apply for a postal vote?")
    postalVoteModel.question.postUrl should be("/register-to-vote/postal-vote")
    postalVoteModel.question.backUrl should be("/register-to-vote/contact")

    postalVoteModel.postCheckboxYes.attributes should be("")
    postalVoteModel.postCheckboxNo.attributes should be("")
    postalVoteModel.deliveryByEmail.attributes should be("")
    postalVoteModel.deliveryByPost.attributes should be("")
    postalVoteModel.emailField.value should be("")

  }

  it should "progress form with no for postal vote" in {
    val partiallyFilledApplicationForm = postalVoteForm.fill(InprogressOrdinary(
      postalVote = Some(PostalVote(
        postalVoteOption = Some(false),
        deliveryMethod = None))))

    val postalVoteModel = postalVoteMustache.transformFormStepToMustacheData(
      partiallyFilledApplicationForm, PostalVoteController.post, Some(ContactController.get))

    postalVoteModel.question.title should be("Do you want to apply for a postal vote?")
    postalVoteModel.question.postUrl should be("/register-to-vote/postal-vote")
    postalVoteModel.question.backUrl should be("/register-to-vote/contact")

    postalVoteModel.postCheckboxYes.attributes should be("")
    postalVoteModel.postCheckboxNo.attributes should be("checked=\"checked\"")
    postalVoteModel.deliveryByEmail.attributes should be("")
    postalVoteModel.deliveryByPost.attributes should be("")
    postalVoteModel.emailField.value should be("")
  }
  
  it should "progress form with yes for postal vote and post for delivery method " + 
    "should produce form with values" in {
    val partiallyFilledApplicationForm = postalVoteForm.fill(InprogressOrdinary(
      postalVote = Some(PostalVote(
        postalVoteOption = Some(true),
        deliveryMethod = Some(PostalVoteDeliveryMethod(deliveryMethod = Some("post"), None))))))

    val postalVoteModel = postalVoteMustache.transformFormStepToMustacheData(
      partiallyFilledApplicationForm, PostalVoteController.post, Some(ContactController.get))

    postalVoteModel.question.title should be("Do you want to apply for a postal vote?")
    postalVoteModel.question.postUrl should be("/register-to-vote/postal-vote")
    postalVoteModel.question.backUrl should be("/register-to-vote/contact")

    postalVoteModel.postCheckboxYes.attributes should be("checked=\"checked\"")
    postalVoteModel.postCheckboxNo.attributes should be("")
    postalVoteModel.deliveryByEmail.attributes should be("")
    postalVoteModel.deliveryByPost.attributes should be("checked=\"checked\"")
    postalVoteModel.emailField.value should be("")
  }  
  
  it should "progress form with yes for postal vote and email for delivery method " + 
    "should produce form with values" in {
    val partiallyFilledApplicationForm = postalVoteForm.fill(InprogressOrdinary(
      postalVote = Some(PostalVote(
        postalVoteOption = Some(true),
        deliveryMethod = Some(PostalVoteDeliveryMethod(deliveryMethod = Some("email"), 
            emailAddress = Some("test@test.com")))))))

    val postalVoteModel = postalVoteMustache.transformFormStepToMustacheData(
      partiallyFilledApplicationForm, PostalVoteController.post, Some(ContactController.get))

    postalVoteModel.question.title should be("Do you want to apply for a postal vote?")
    postalVoteModel.question.postUrl should be("/register-to-vote/postal-vote")
    postalVoteModel.question.backUrl should be("/register-to-vote/contact")

    postalVoteModel.postCheckboxYes.attributes should be("checked=\"checked\"")
    postalVoteModel.postCheckboxNo.attributes should be("")
    postalVoteModel.deliveryByEmail.attributes should be("checked=\"checked\"")
    postalVoteModel.deliveryByPost.attributes should be("")
    postalVoteModel.emailField.value should be("test@test.com")
  }  
}
