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
          id = "postCheckboxYesId",
          name = "postCheckboxYesName",
          attributes = "foo=\"foo\""
        ),
        postCheckboxNo = Field(
          id = "postCheckboxNoId",
          name = "postCheckboxNoName",
          attributes = ""
        ),
        deliveryByEmail = Field(
          id = "deliveryByEmailId",
          name = "deliveryByEmailName",
          value = "deliveryByEmailValue",
          attributes = "foo=\"foo\""
        ),
        deliveryByPost = Field(
          id = "deliveryByPostId",
          name = "deliveryByPostName",
          value = "deliveryByPostValue",
          attributes = ""
        ),
        emailField = Field(
          id = "emailFieldId",
          name = "emailFieldName",
          value = "test@test.com"
        ),
        deliveryMethodValid = "valid"
      )

      val html = Mustache.render("ordinary/postalVote", data)
      val doc = Jsoup.parse(html.toString)

      val postCheckboxYesInput = doc.select("input[id=postCheckboxYesId]").first()
      postCheckboxYesInput.attr("id") should be("postCheckboxYesId")
      postCheckboxYesInput.attr("name") should be("postCheckboxYesName")
      postCheckboxYesInput.attr("foo") should include("foo")

      val postCheckboxNoInput = doc.select("input[id=postCheckboxNoId]").first()
      postCheckboxNoInput.attr("id") should be("postCheckboxNoId")
      postCheckboxNoInput.attr("name") should be("postCheckboxNoName")
      
      val deliveryByEmailInput = doc.select("input[id=deliveryByEmailId]").first()
      deliveryByEmailInput.attr("id") should be("deliveryByEmailId")
      deliveryByEmailInput.attr("name") should be("deliveryByEmailName")
      deliveryByEmailInput.attr("value") should include("deliveryByEmailValue")
      deliveryByEmailInput.attr("foo") should include("foo")
      
      val deliveryByPostInput = doc.select("input[id=deliveryByPostId]").first()
      deliveryByPostInput.attr("id") should be("deliveryByPostId")
      deliveryByPostInput.attr("name") should be("deliveryByPostName")
      
      val emailFieldInput = doc.select("input[id=emailFieldId]").first()
      emailFieldInput.attr("id") should be("emailFieldId")
      emailFieldInput.attr("name") should be("emailFieldName")
      emailFieldInput.attr("value") should be("test@test.com")
    }
  }
}