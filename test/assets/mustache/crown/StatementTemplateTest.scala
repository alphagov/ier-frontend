package assets.mustache.crown

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.transaction.crown.statement.StatementMustache

class StatementTemplateTest
  extends FlatSpec
  with StatementMustache
  with Matchers {

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {
      val data = StatementModel(
        question = Question(),
        crown = Field(
          id = "crownId",
          classes = "crownClasses"
        ),
        crownServant = Field(
          id = "crownServantId",
          name = "crownServantName",
          classes = "crownServantClasses",
          value = "crownServantValue",
          attributes = "foo=\"foo\""
        ),
        crownPartner = Field(
          id = "crownPartnerId",
          name = "crownPartnerName",
          classes = "crownPartnerClasses",
          value = "crownPartnerValue",
          attributes = "foo=\"foo\""
        ),
        council = Field(
          id = "councilId",
          classes = "councilClasses"
        ),
        councilEmployee = Field(
          id = "councilEmployeeId",
          name = "councilEmployeeName",
          classes = "councilEmployeeClasses",
          value = "councilEmployeeValue",
          attributes = "foo=\"foo\""
        ),
        councilPartner = Field(
          id = "councilPartnerId",
          name = "councilPartnerName",
          classes = "councilPartnerClasses",
          value = "councilPartnerValue",
          attributes = "foo=\"foo\""
        )
      )

      val html = Mustache.render("crown/statement", data)
      val doc = Jsoup.parse(html.toString)


      val crownFieldsetLabel = doc.select("label[for=crownId]").first()
      crownFieldsetLabel should not be(null)
      crownFieldsetLabel.attr("for") should be("crownId")

      val crownFieldset = doc.select("fieldset[id=crownId]").first()
      crownFieldset should not be(null)
      crownFieldset.attr("id") should be("crownId")
      crownFieldset.attr("class") should be("crownClasses")

      val crownServantLabel = crownFieldset.select("label[for=crownServantId]").first()
      crownServantLabel should not be(null)
      crownServantLabel.attr("for") should be("crownServantId")
      
      val crownServantInput = crownServantLabel.select("input").first()
      crownServantInput should not be(null)
      crownServantInput.attr("id") should be("crownServantId")
      crownServantInput.attr("name") should be("crownServantName")
      crownServantInput.attr("class") should be("crownServantClasses")
      crownServantInput.attr("value") should be("crownServantValue")
      crownServantInput.attr("foo") should be("foo")

      val crownPartnerLabel = crownFieldset.select("label[for=crownPartnerId]").first()
      crownPartnerLabel should not be(null)
      crownPartnerLabel.attr("for") should be("crownPartnerId")
      
      val crownPartnerInput = crownPartnerLabel.select("input").first()
      crownPartnerInput should not be(null)
      crownPartnerInput.attr("id") should be("crownPartnerId")
      crownPartnerInput.attr("name") should be("crownPartnerName")
      crownPartnerInput.attr("class") should be("crownPartnerClasses")
      crownPartnerInput.attr("value") should be("crownPartnerValue")
      crownPartnerInput.attr("foo") should be("foo")


      val councilFieldsetLabel = doc.select("label[for=councilId]").first()
      councilFieldsetLabel should not be(null)
      councilFieldsetLabel.attr("for") should be("councilId")

      val councilFieldset = doc.select("fieldset[id=councilId]").first()
      councilFieldset should not be(null)
      councilFieldset.attr("id") should be("councilId")
      councilFieldset.attr("class") should be("councilClasses")

      val councilEmployeeLabel = councilFieldset.select("label[for=councilEmployeeId]").first()
      councilEmployeeLabel should not be(null)
      councilEmployeeLabel.attr("for") should be("councilEmployeeId")
      
      val councilEmployeeInput = councilEmployeeLabel.select("input").first()
      councilEmployeeInput should not be(null)
      councilEmployeeInput.attr("id") should be("councilEmployeeId")
      councilEmployeeInput.attr("name") should be("councilEmployeeName")
      councilEmployeeInput.attr("class") should be("councilEmployeeClasses")
      councilEmployeeInput.attr("value") should be("councilEmployeeValue")
      councilEmployeeInput.attr("foo") should be("foo")

      val councilPartnerLabel = councilFieldset.select("label[for=councilPartnerId]").first()
      councilPartnerLabel should not be(null)
      councilPartnerLabel.attr("for") should be("councilPartnerId")
      
      val councilPartnerInput = councilPartnerLabel.select("input").first()
      councilPartnerInput should not be(null)
      councilPartnerInput.attr("id") should be("councilPartnerId")
      councilPartnerInput.attr("name") should be("councilPartnerName")
      councilPartnerInput.attr("class") should be("councilPartnerClasses")
      councilPartnerInput.attr("value") should be("councilPartnerValue")
      councilPartnerInput.attr("foo") should be("foo")
    }
  }
}
