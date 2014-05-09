package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import org.joda.time.DateTime
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{
  Name,
  PreviousName,
  WaysToVote}
import uk.gov.gds.ier.transaction.overseas.confirmation.ConfirmationForms
import org.joda.time.DateTime
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.transaction.shared.BlockContent

class WaysToVoteBlocksTests
  extends FlatSpec
  with Matchers
  with ConfirmationForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  "application form with filled way to vote as by-post" should
    "generate confirmation mustache model with correctly rendered way to vote type" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByPost)),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByPost,
        postalVoteOption = Some(true),
        deliveryMethod = Some(PostalVoteDeliveryMethod(
          deliveryMethod = Some("post"),
          emailAddress = None
        ))
      ))
    ))
    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)
    val model = confirmation.waysToVote
    model.content should be(BlockContent(List(
      "I want to vote by post",
      "Send me an application form in the post")))
    model.editLink should be("/register-to-vote/overseas/edit/ways-to-vote")
  }

  "application form with filled way to vote as by-proxy" should
    "generate confirmation mustache model with correctly rendered way to vote type" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByProxy)),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByProxy,
        postalVoteOption = Some(true),
        deliveryMethod = Some(PostalVoteDeliveryMethod(
          deliveryMethod = Some("post"),
          emailAddress = None
        ))
      ))
    ))
    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)
    val model = confirmation.waysToVote
    model.content should be(BlockContent(List(
      "I want to vote by proxy (someone else voting for me)",
      "Send me an application form in the post")))
    model.editLink should be("/register-to-vote/overseas/edit/ways-to-vote")
  }

  "application form with filled way to vote as in-person" should
    "generate confirmation mustache model with correctly rendered way to vote type" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      waysToVote = Some(WaysToVote(WaysToVoteType.InPerson))))
    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)
    val model = confirmation.waysToVote
    model.content should be(BlockContent(
      "I want to vote in person, at a polling station"))
    model.editLink should be("/register-to-vote/overseas/edit/ways-to-vote")
  }

  "application form with filled way to vote as in-person with no delivery method" should
    "generate block content stating I don't need 'an' application form" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      waysToVote = Some(WaysToVote(WaysToVoteType.InPerson)),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.InPerson,
        postalVoteOption = Some(false),
        None
      ))
    ))
    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)
    val model = confirmation.waysToVote
    model.content should be(BlockContent(List(
      "I want to vote in person, at a polling station",
      "I do not need an application form")))
    model.editLink should be("/register-to-vote/overseas/edit/ways-to-vote")
  }

  "application form with filled way to vote by post with no delivery method" should
    "generate block content stating I don't need 'a postal vote' application form" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByPost)),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByPost,
        postalVoteOption = Some(false),
        None
      ))
    ))
    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)
    val model = confirmation.waysToVote
    model.content should be(BlockContent(List(
      "I want to vote by post",
      "I do not need a postal vote application form")))
    model.editLink should be("/register-to-vote/overseas/edit/ways-to-vote")
  }
}
