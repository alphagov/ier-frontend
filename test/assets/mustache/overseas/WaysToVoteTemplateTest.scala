package assets.mustache.overseas

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.transaction.overseas.waysToVote.WaysToVoteMustache

/**
 * Test rendering of Mustache template from given model
 */
class WaysToVoteTemplateTest
  extends FlatSpec
  with WaysToVoteMustache
  with Matchers {

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {
      val data = new WaysToVoteModel(
        question = Question(
          postUrl = "http://some.server/post_url",
          backUrl = "http://some.server/back_url",
          showBackUrl = true,
          number = "123",
          title = "Page title ABC"
        ),
        byPost = Field(
          id = "waysToVote_wayType_by-post",
          name = "waysToVote.wayType",
          classes = "byPostClass byPostClass2",
          value = "by-post",
          attributes = "checked=\"checked1\""
        ),
        byProxy = Field(
          id = "waysToVote_wayType_by-proxy",
          name = "waysToVote.wayType",
          classes = "byProxyClass byProxyClass2",
          value = "by-proxy",
          attributes = "checked=\"checked2\""
        ),
        inPerson = Field(
          id = "waysToVote_wayType_in-person",
          name = "waysToVote.wayType",
          classes = "inPersonClass inPersonClass2",
          value = "in-person",
          attributes = "checked=\"checked3\""
        )
      )

      val html = Mustache.render("overseas/waysToVote", data)
      val doc = Jsoup.parse(html.toString)

      { // by post option
        doc.select("label[for=waysToVote_wayType_by-post]").size() should be(1)
        doc.select("input#waysToVote_wayType_by-post").size() should be(1)
        val r = doc.select("input#waysToVote_wayType_by-post").first()
        r.attr("id") should be("waysToVote_wayType_by-post")
        r.attr("name") should be("waysToVote.wayType")
        r.attr("value") should be("by-post")
        r.attr("class") should include("byPostClass")
        r.attr("class") should include("byPostClass2")
        r.attr("checked") should be("checked1")
      }

      { // by proxy option
        doc.select("label[for=waysToVote_wayType_by-proxy]").size() should be(1)
        doc.select("input#waysToVote_wayType_by-proxy").size() should be(1)
        val r = doc.select("input#waysToVote_wayType_by-proxy").first()
        r.attr("id") should be("waysToVote_wayType_by-proxy")
        r.attr("name") should be("waysToVote.wayType")
        r.attr("value") should be("by-proxy")
        r.attr("class") should include("byProxyClass")
        r.attr("class") should include("byProxyClass2")
        r.attr("checked") should be("checked2")
      }

      { // in person option, aka 'In the UK, at a polling station'
        doc.select("label[for=waysToVote_wayType_in-person]").size() should be(1)
        doc.select("input#waysToVote_wayType_in-person").size() should be(1)
        val r = doc.select("input#waysToVote_wayType_in-person").first()
        r.attr("id") should be("waysToVote_wayType_in-person")
        r.attr("name") should be("waysToVote.wayType")
        r.attr("value") should be("in-person")
        r.attr("class") should include("inPersonClass")
        r.attr("class") should include("inPersonClass2")
        r.attr("checked") should be("checked3")
      }

      { // page
        val f = doc.select("form").first() // there should be only one form in the template
        f should not be(null)
        f.attr("action") should be ("http://some.server/post_url")

        val h = doc.select("header").first() // there should be only one header in the template
        h should not be(null)
        h.text should include ("123")
        h.text should include ("Page title ABC")
      }
    }
  }
}
