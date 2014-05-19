package assets.mustache.complete

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.transaction.complete.CompleteMustache
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.guice.WithRemoteAssets
import uk.gov.gds.ier.assets.RemoteAssets
import org.specs2.mock.Mockito
import uk.gov.gds.ier.test.WithMockRemoteAssets


class CompleteTemplateTest
  extends FlatSpec
  with CompleteMustache
  with WithMockRemoteAssets
  with StepMustache
  with Matchers
  with Mockito {

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {
      val data = new Complete.CompleteModel(
        authorityName = "election authority 123",
        authorityUrl = Some("http://authority123.gov.uk/contactUs"),
        refNumber = Some("123457689013"),
        hasOtherAddress = true,
        backToStartUrl = "/register-to-vote/start"
      )

      val html = Mustache.render("complete", data)
      val renderedOutput = html.toString
      val doc = Jsoup.parse(renderedOutput)

      doc.select("a[href=" + data.authorityUrl.get + "]").size() should be(1)
      doc.select("a[href=" + data.backToStartUrl + "]").size() should be(1)

      renderedOutput should include(data.refNumber.get)
      renderedOutput should include(data.authorityName)
    }
  }
}
