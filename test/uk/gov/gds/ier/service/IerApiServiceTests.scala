package uk.gov.gds.ier.service

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.client.IerApiClient
import uk.gov.gds.ier.model._
import org.joda.time.DateTime
import uk.gov.gds.ier.test.TestHelpers
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import uk.gov.gds.ier.digest.ShaHashProvider
import uk.gov.gds.ier.model.InprogressApplication
import uk.gov.gds.ier.model.Success
import uk.gov.gds.ier.model.Fail
import scala.Some
import uk.gov.gds.common.model.{Ero, LocalAuthority}

class IerApiServiceTests
  extends FlatSpec
  with Matchers
  with TestHelpers
  with MockitoSugar {

  class MockConfig extends Config {
    override def ierApiUrl = "testUrl"
    override def ierApiToken = "123457890"
  }

  val successMessage = Success(s"""
    {
      "id" : "123456",
      "createdAt" : "${DateTime.now.toString()}",
      "status" : "unprocessed",
      "source" : "web",
      "gssCode" : "ab12346"
    }
  """)

  it should "contain the application type field" in {
    class FakeApiClient extends IerApiClient(new MockConfig) {
      override def post(url:String, content:String, headers: (String, String)*) : ApiResponse = {
        if (url.contains("testUrl")) {
          content should include("applicationType\":\"ordinary\"")
          successMessage
        } else {
          Fail("Bad Url")
        }
      }
    }
    val mockPlaces = mock[PlacesService]
    val mockSha = mock[ShaHashProvider]
    val isoService = new IsoCountryService

    val service = new ConcreteIerApiService(new FakeApiClient, jsonSerialiser,
      new MockConfig, mockPlaces, mockSha, isoService)

    val application = InprogressApplication(
      nationality = Some(Nationality(
        british = Some(true),
        irish = Some(true),
        hasOtherCountry = Some(true),
        otherCountries = List("France")
      ))
    )

    service.submitApplication(None, application, None)

  }

  it should "convert country names to ISO codes" in {
    class FakeApiClient extends IerApiClient(new MockConfig) {
      override def post(url:String, content:String, headers: (String, String)*) : ApiResponse = {
        if (url.contains("testUrl")) {
          content should include("GB")
          content should not include("British")
          content should include("IE")
          content should not include("Irish")
          content should include("FR")
          content should not include("France")
          successMessage
        } else {
          Fail("Bad Url")
        }
      }
    }
    val mockPlaces = mock[PlacesService]
    val mockSha = mock[ShaHashProvider]
    val isoService = new IsoCountryService

    val service = new ConcreteIerApiService(new FakeApiClient, jsonSerialiser,
      new MockConfig, mockPlaces, mockSha, isoService)

    val application = InprogressApplication(
      nationality = Some(Nationality(
        british = Some(true),
        irish = Some(true),
        hasOtherCountry = Some(true),
        otherCountries = List("France")
      ))
    )

    service.submitApplication(None, application, None)
  }
}
