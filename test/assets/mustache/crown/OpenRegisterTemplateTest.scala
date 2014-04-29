package assets.mustache.crown

import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import org.jsoup.Jsoup
import play.api.test.Helpers._
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.transaction.crown.openRegister.OpenRegisterMustache

class OpenRegisterTemplateTest
  extends FlatSpec
  with OpenRegisterMustache
  with StepMustache
  with Matchers {

  it should "properly render all properties from the model" in {

    running(FakeApplication()) {
      val data = OpenRegisterModel(
        question = Question(postUrl = "/whatever-url",
        number = "1",
        title = "open register title"
        ),
        openRegister = Field(
          id = "openRegisterId",
          name = "openRegisterName",
          value = "false"
        )
      )

      val html = Mustache.render("crown/openRegister", data)
      val doc = Jsoup.parse(html.toString)

      val openRegisterInput = doc.select("input#openRegisterId").first()
      openRegisterInput should not be(null)
      openRegisterInput.attr("id") should be("openRegisterId")
      openRegisterInput.attr("name") should be("openRegisterName")
      openRegisterInput.attr("value") should be("false")
    }
  }
}
