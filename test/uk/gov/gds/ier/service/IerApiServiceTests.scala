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
import uk.gov.gds.ier.model.Success
import uk.gov.gds.ier.model.Fail
import scala.Some
import uk.gov.gds.ier.service.apiservice.{EroAuthorityDetails, IerApiApplicationResponse, IerApiService, ConcreteIerApiService}
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

/**
 * Test {@link IerApiService} submission methods where IER-API is emulated with FakeApiClient.
 *
 * For test of transformation of application data to API request format see
 * {@link OrdinaryApplicationTests}, {@link OverseasApplicationTests} ..
 */
class IerApiServiceTests
  extends FlatSpec
  with Matchers
  with IerApiServiceTestsHelper
  with MockitoSugar {

  val successMessage = Success(s"""
  {
    "id": "5360fe69036424d9ec0a1657",
    "localAuthority": {
      "name": "Local authority name",
      "urls": ["url1", "url2"],
      "email": "some@email.com",
      "phone": "0123456789",
      "addressLine1": "line one",
      "addressLine2": "line two",
      "addressLine3": "line three",
      "addressLine4": "line four",
      "postcode": "WR26NJ"
    }
  }
  """, 0)

  "submitOrdinaryApplication" should
    "deserialize result correctly and return expected response" in {
    val application = completeOrdinaryApplication

    val r = fakeServiceCall(
      requestJson => {
        requestJson should include("applicationType\":\"ordinary\"")
        requestJson should include("John")
        requestJson should include("Smith")
        successMessage
      }
    ).submitOrdinaryApplication(None, application, None, Some("1234"))

    r should be(IerApiApplicationResponse(
      id = "5360fe69036424d9ec0a1657",
      localAuthority = EroAuthorityDetails(
        name = "Local authority name",
        urls = "url1" :: "url2" :: Nil,
        email = Some("some@email.com"),
        phone = Some("0123456789"),
        addressLine1 = Some("line one"),
        addressLine2 = Some("line two"),
        addressLine3 = Some("line three"),
        addressLine4 = Some("line four"),
        postcode = Some("WR26NJ")
      )
    ))
  }

  "submitOrdinaryApplication with specified IP and refNum" should
    "deserialize result correctly and return expected response" in {
    val application = completeOrdinaryApplication

    val r = fakeServiceCall(
      requestJson => {
        requestJson should include("applicationType\":\"ordinary\"")
        requestJson should include("John")
        requestJson should include("Smith")
        successMessage
      }
    ).submitOrdinaryApplication(Some("127.0.0.1"), application, Some("55631D"), Some("1234"))

    r should be(IerApiApplicationResponse(
      id = "5360fe69036424d9ec0a1657",
      localAuthority = EroAuthorityDetails(
        name = "Local authority name",
        urls = "url1" :: "url2" :: Nil,
        email = Some("some@email.com"),
        phone = Some("0123456789"),
        addressLine1 = Some("line one"),
        addressLine2 = Some("line two"),
        addressLine3 = Some("line three"),
        addressLine4 = Some("line four"),
        postcode = Some("WR26NJ")
      )
    ))
  }

  "submitOverseasApplication" should
    "deserialize result correctly and return expected response" in {
    val application = completeOverseasApplication

    val r = fakeServiceCall(
      requestJson => {
        requestJson should include("applicationType\":\"overseas\"")
        requestJson should include("John")
        requestJson should include("Smith")
        successMessage
      }
    ).submitOverseasApplication(None, application, None, Some("1234"))

    r should be(IerApiApplicationResponse(
      id = "5360fe69036424d9ec0a1657",
      localAuthority = EroAuthorityDetails(
        name = "Local authority name",
        urls = "url1" :: "url2" :: Nil,
        email = Some("some@email.com"),
        phone = Some("0123456789"),
        addressLine1 = Some("line one"),
        addressLine2 = Some("line two"),
        addressLine3 = Some("line three"),
        addressLine4 = Some("line four"),
        postcode = Some("WR26NJ")
      )
    ))
  }

  "submitCrownApplication" should
    "deserialize result correctly and return expected response" in {
    val application = completeCrownApplication

    val r = fakeServiceCall(
      requestJson => {
        requestJson should include("applicationType\":\"crown\"")
        requestJson should include("John")
        requestJson should include("Smith")
        successMessage
      }
    ).submitCrownApplication(None, application, None, Some("1234"))

    r should be(IerApiApplicationResponse(
      id = "5360fe69036424d9ec0a1657",
      localAuthority = EroAuthorityDetails(
        name = "Local authority name",
        urls = "url1" :: "url2" :: Nil,
        email = Some("some@email.com"),
        phone = Some("0123456789"),
        addressLine1 = Some("line one"),
        addressLine2 = Some("line two"),
        addressLine3 = Some("line three"),
        addressLine4 = Some("line four"),
        postcode = Some("WR26NJ")
      )
    ))
  }

  "submitForcesApplication" should
    "deserialize result correctly and return expected response" in {
    val application = completeForcesApplication

    val r = fakeServiceCall(
      requestJson => {
        requestJson should include("applicationType\":\"forces\"")
        requestJson should include("John")
        requestJson should include("Smith")
        successMessage
      }
    ).submitForcesApplication(None, application, None, Some("1234"))

    r should be(IerApiApplicationResponse(
      id = "5360fe69036424d9ec0a1657",
      localAuthority = EroAuthorityDetails(
        name = "Local authority name",
        urls = "url1" :: "url2" :: Nil,
        email = Some("some@email.com"),
        phone = Some("0123456789"),
        addressLine1 = Some("line one"),
        addressLine2 = Some("line two"),
        addressLine3 = Some("line three"),
        addressLine4 = Some("line four"),
        postcode = Some("WR26NJ")
      )
    ))
  }
}
