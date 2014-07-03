package assets.mustache.complete

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.transaction.complete.CompleteMustache
import uk.gov.gds.ier.test.{WithMockConfig, WithMockRemoteAssets}
import org.mockito.Mockito._
import org.mockito.Matchers._
import uk.gov.gds.ier.service.apiservice.EroAuthorityDetails
import play.api.mvc.Call

class CompleteTemplateTest
  extends FlatSpec
  with WithMockConfig
  with WithMockRemoteAssets
  with CompleteMustache
  with Matchers {

  when(config.ordinaryStartUrl).thenReturn("/register-to-vote")
  when(remoteAssets.messages(anyString())).thenReturn(Call("GET", "/assets/messages/en"))
  when(remoteAssets.templatePath).thenReturn("/assets/template")
  when(remoteAssets.assetsPath).thenReturn("/assets")

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {

      val mustache = new Complete.CompletePage(
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
        refNumber = "123457689013",
        hasOtherAddress = true,
        backToStartUrl = "/register-to-vote/start",
        showEmailConfirmation = true
      )

      val html = mustache.render()
      val renderedOutput = html.toString
      val doc = Jsoup.parse(renderedOutput)

      doc.select("a[href=" + mustache.authorityUrl.get + "]").size() should be(1)
      doc.select("a[href=" + mustache.backToStartUrl + "]").size() should be(1)

      renderedOutput should include(mustache.refNumber)
      renderedOutput should include(mustache.authorityName)
    }
  }
}
