package assets.mustache.complete

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.transaction.complete.CompleteMustache
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithConfig}
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.config.Config
import org.specs2.mock.Mockito
import uk.gov.gds.ier.test.WithMockRemoteAssets
import uk.gov.gds.ier.service.apiservice.EroAuthorityDetails


class CompleteTemplateTest
  extends FlatSpec
  with CompleteMustache
  with WithConfig
  with WithMockRemoteAssets
  with StepMustache
  with Matchers
  with Mockito {

  val config = new Config()

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {
      val data = new Complete.CompletePage(
        authority = Some(EroAuthorityDetails(
          name = "election authority 123",
          urls = List("http://authority123.gov.uk/contactUs"),
          email = None,
          phone = None,
          addressLine1 = None,
          addressLine2 = None,
          addressLine3 = None,
          addressLine4 = None,
          postcode = None
        )),
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
