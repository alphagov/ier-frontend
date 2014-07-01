package uk.gov.gds.ier.localAuthority

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import uk.gov.gds.ier.validation.ErrorMessages
import uk.gov.gds.ier.validation.FormKeys
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.test.WithMockConfig
import uk.gov.gds.ier.test.WithMockRemoteAssets
import uk.gov.gds.ier.serialiser.WithSerialiser
import org.mockito.Mockito._
import org.mockito.Matchers._
import play.api.mvc.Call
import uk.gov.gds.ier.model.LocalAuthorityContactDetails
import play.api.test.Helpers._

class LocalAuthorityMustacheTests
  extends FlatSpec
  with Matchers
  with LocalAuthorityLookupForm
  with LocalAuthorityMustache
  with ErrorMessages
  with FormKeys
  with TestHelpers
  with WithMockConfig
  with WithMockRemoteAssets
  with WithSerialiser{

  val serialiser = jsonSerialiser

  behavior of "LocalAutorityPage"
  it should "render the Local Authority information " in {
    running(FakeApplication()) {
      val authorityDetails = LocalAuthorityContactDetails(
        name = Some("authority name"),
        url = Some("http://localhost"),
        addressLine1 = Some("addressLine1"),
        addressLine2 = Some("addressLine2"),
        postcode = Some("ab123cd"),
        emailAddress = Some("test@test.com"),
        phoneNumber = Some("123456")
      )
      val authorityPage = LocalAuthorityPage(Some(authorityDetails), Some("/test"))
      when(remoteAssets.messages(any[String])).thenReturn(Call("GET", "/assests/messages"))
      val htmlBody = authorityPage.render.body

      htmlBody should include("authority name")
      htmlBody should include("http://localhost")
      htmlBody should include("addressLine1")
      htmlBody should include("addressLine2")
      htmlBody should include("ab123cd")
      htmlBody should include("test@test.com")
      htmlBody should include("123456")
    }
  }

  behavior of "LocalAuthorityLookupPage"
  it should "render the lookup page" in {
    running(FakeApplication()) {
    val lookupPage = LocalAuthorityLookupPage(
      postcode = Field(id = "postcode_id", name = "postcode_name", classes = "postcode_classes",
        value = "postcode_value"),
      sourcePath = Some("/sourcePath"),
      postUrl = "/postUrl"
    )
    when(remoteAssets.messages(any[String])).thenReturn(Call("GET", "/assests/messages"))
    val htmlBody = lookupPage.render.body

    htmlBody should include("postcode_name")
    htmlBody should include("postcode_id")
    htmlBody should include("postcode_classes")
    htmlBody should include("postcode_value")
    htmlBody should include("/postUrl")
    }
  }

}