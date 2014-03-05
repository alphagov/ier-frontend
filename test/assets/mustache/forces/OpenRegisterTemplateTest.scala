package assets.mustache.forces

import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import org.jsoup.Jsoup
import play.api.test.Helpers._
import uk.gov.gds.ier.transaction.forces.openRegister.OpenRegisterMustache

class OpenRegisterTemplateTest
  extends FlatSpec
  with OpenRegisterMustache
  with Matchers {

  it should "properly render all properties from the model" in {

    running(FakeApplication()) {
      val data = OpenRegisterModel(
        question = Question(postUrl = "/whatever-url",
        backUrl = "",
        number = "1",
        title = "open register title"
        ),
        openRegister = Field(
          id = "openRegister_optIn",
          name = "openRegister.optIn",
          value = "false"
        )
      )

      val html = Mustache.render("forces/openRegister", data)
      val doc = Jsoup.parse(html.toString)

      val openRegisterInput = doc.select("input#openRegister_optIn").first()
      openRegisterInput should not be(null)
      openRegisterInput.attr("id") should be("openRegister_optIn")
      openRegisterInput.attr("name") should be("openRegister.optIn")
      openRegisterInput.attr("value") should be("false")
    }
  }
}