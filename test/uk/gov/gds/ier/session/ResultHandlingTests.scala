package uk.gov.gds.ier.session

import org.scalatest.{Matchers, FlatSpec}
import play.api.test.FakeRequest
import uk.gov.gds.ier.guice.{WithConfig, WithEncryption}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.security.{Base64EncodingService, EncryptionService}
import uk.gov.gds.ier.controller.MockConfig

class ResultHandlingTests
  extends FlatSpec
  with Matchers
  with ResultHandling
  with WithEncryption
  with WithConfig
  with WithSerialiser
  with TestHelpers {

  val serialiser = jsonSerialiser
  val config = new MockConfig
  val encryptionService = new EncryptionService (new Base64EncodingService, config)

  behavior of "ResultHandling.getDomain"

  it should "return None if on localhost" in {
    val request = FakeRequest("GET", "/").withHeaders("Host" -> "localhost:9000")
    getDomain(request) should be(None)
  }

  it should "return the HOST header from the request" in {
    val request = FakeRequest("GET", "/").withHeaders("Host" -> "foo.com")
    getDomain(request) should be(Some("foo.com"))
  }

  it should "strip port number from the domain" in {
    val request = FakeRequest("GET", "/").withHeaders("Host" -> "foo.com:1234")
    getDomain(request) should be(Some("foo.com"))
  }

}
