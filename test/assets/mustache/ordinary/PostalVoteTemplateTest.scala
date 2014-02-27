package assets.mustache.ordinary

import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import org.jsoup.Jsoup
import play.api.test.Helpers._
import uk.gov.gds.ier.transaction.ordinary.postalVote.PostalVoteMustache

class PostalVoteTemplateTest
  extends FlatSpec
  with PostalVoteMustache
  with Matchers {

  it should "properly render all properties from the model" in {

    running(FakeApplication()) {
      val data = PostalVoteModel(
        question = Question(postUrl = "/whatever-url",
        backUrl = "",
        number = "1",
        title = "postal vote title"
        ),
        postCheckboxYes = Field(
          id = "postalVote_optIn_true",
          name = "postalVote.OptIn",
          attributes = "foo=\"foo\""
        ),
        postCheckboxNo = Field(
          id = "postalVote_optIn_false",
          name = "postalVote.OptIn",
          attributes = ""
        ),
        deliveryByEmail = Field(
          id = "postalVote_deliveryMethod_methodName_email",
          name = "postalVote.deliveryMethod.methodName",
          value = "email",
          attributes = "foo=\"foo\""
        ),
        deliveryByPost = Field(
          id = "postalVote_deliveryMethod_methodName_post",
          name = "postalVote.deliveryMethod.methodName",
          value = "post",
          attributes = ""
        ),
        emailField = Field(
          id = "postalVote_deliveryMethod_emailAddress",
          name = "postalVote.deliveryMethod.emailAddress",
          value = "test@test.com"
        )
      )

      val html = Mustache.render("ordinary/postalVote", data)
      val doc = Jsoup.parse(html.toString)

      val postCheckboxYesInput = doc.select("input[id=postalVote_optIn_true]").first()
      postCheckboxYesInput.attr("id") should be("postalVote_optIn_true")
      postCheckboxYesInput.attr("name") should be("postalVote.OptIn")
      postCheckboxYesInput.attr("foo") should include("foo")

      val postCheckboxNoInput = doc.select("input[id=postalVote_optIn_false]").first()
      postCheckboxNoInput.attr("id") should be("postalVote_optIn_false")
      postCheckboxNoInput.attr("name") should be("postalVote.OptIn")
      
      val deliveryByEmailInput = doc.select("input[id=postalVote_deliveryMethod_methodName_email]").first()
      deliveryByEmailInput.attr("id") should be("postalVote_deliveryMethod_methodName_email")
      deliveryByEmailInput.attr("name") should be("postalVote.deliveryMethod.methodName")
      deliveryByEmailInput.attr("foo") should include("foo")
      
      val deliveryByPostInput = doc.select("input[id=postalVote_deliveryMethod_methodName_post]").first()
      deliveryByPostInput.attr("id") should be("postalVote_deliveryMethod_methodName_post")
      deliveryByPostInput.attr("name") should be("postalVote.deliveryMethod.methodName")
      
      val emailFieldInput = doc.select("input[id=postalVote_deliveryMethod_emailAddress]").first()
      emailFieldInput.attr("id") should be("postalVote_deliveryMethod_emailAddress")
      emailFieldInput.attr("name") should be("postalVote.deliveryMethod.emailAddress")
      emailFieldInput.attr("value") should be("test@test.com")
    }
  }
}