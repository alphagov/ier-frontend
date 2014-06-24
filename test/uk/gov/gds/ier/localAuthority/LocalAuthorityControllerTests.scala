package uk.gov.gds.ier.localAuthority

import uk.gov.gds.ier.test.TestHelpers
import org.scalatest.{ Matchers, FlatSpec }
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.model.PartialAddress
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.transaction.ordinary.address.AddressStep
import uk.gov.gds.ier.step.GoTo
import controllers.routes.ExitController
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import uk.gov.gds.ier.service.apiservice.ConcreteIerApiService
import uk.gov.gds.ier.client.LocateApiClient
import uk.gov.gds.ier.controller.MockConfig
import uk.gov.gds.ier.model.ApiResponse
import uk.gov.gds.ier.model.Success
import uk.gov.gds.ier.model.Fail
import uk.gov.gds.ier.service.LocateService

class LocalAuthorityControllerTests
  extends FlatSpec
  with Matchers
  with TestHelpers
  with MockitoSugar {

  behavior of "LocalAuthorityController.showLookup"
  it should "display the lookup page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/local-authority/lookup").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Contact your local Electoral Registration Office")
      contentAsString(result) should include("/register-to-vote/local-authority/lookup")
    }
  }

  it should "return None if there is no gssCode exists in the cookie" in {
    running(FakeApplication()) {
      val mockedAddressService = mock[AddressService]
      val mockedIerApiService = mock[ConcreteIerApiService]
      val mockedJsonSerialiser = mock[JsonSerialiser]
      val mockedConfig = mock[Config]
      val mockedEncryptionService = mock[EncryptionService]
      val mockedRemoteAssets = mock[RemoteAssets]

      val localAuthorityController = new LocalAuthorityController(
        mockedIerApiService,
        mockedAddressService,
        mockedJsonSerialiser,
        mockedEncryptionService,
        mockedConfig,
        mockedRemoteAssets
      )

      val result = localAuthorityController.getGssCode(Some("/register-to-vote/name"), FakeRequest())
      result should be(None)
    }
  }

//  it should "redirect to the show local authority page" in {
//    running(FakeApplication()) {
//      val mockedAddressService = mock[AddressService]
//      val mockedIerApiService = mock[ConcreteIerApiService]
//      val mockedJsonSerialiser = mock[JsonSerialiser]
//      val mockedConfig = mock[Config]
//      val mockedEncryptionService = mock[EncryptionService]
//      val mockedRemoteAssets = mock[RemoteAssets]
//
//      val localAuthorityController = new LocalAuthorityController(
//        mockedIerApiService,
//        mockedAddressService,
//        mockedJsonSerialiser,
//        mockedEncryptionService,
//        mockedConfig,
//        mockedRemoteAssets
//      )
//
//      val result = localAuthorityController.showLookup(Some("/register-to-vote/name"))(FakeRequest())
//    }
//
//  }

  //  behavior of "LocalAuthorityController.doLookup"
  //  it should "lookup the postcode and redirect to the show authority info page" in {
  //
  //    val mockedAddressService = mock[AddressService]
  //    val postcode = "ab12 3cd"
  //    val gssCode = "a123456"
  //
  //    val Some(result) = route(
  //      FakeRequest(POST, "/register-to-vote/local-authority/lookup")
  //      .withIerSession()
  //      .withFormUrlEncodedBody("postcode" -> postcode)
  //    )
  //
  //    println(result)
  //    status(result) should be(SEE_OTHER)
  //    redirectLocation(result).get should be("register-to-vote/local-authority/" + gssCode)
  //  }

  //  behavior of "LocalAuthorityController.doLookup"
  //  it should "lookup the postcode and redirect to the show authority info page" in {
  //
  //    val postcode = "ab123cd"
  //    val gssCode = "a123456"
  //
  //    running(FakeApplication()) {
  //    class FakeApiClient extends LocateApiClient(new MockConfig) {
  //      override def get(url: String, headers: (String, String)*) : ApiResponse = {
  //        println (url)
  //        if (url == ("https://preview-verification-locate-api.ertp.alphagov.co.uk/locate/authority?postcode=" + postcode)) {
  //          Success("""
  //            {
  //              "gssCode": gssCode
  //            }
  //          """, 0)
  //        } else {
  //          Fail("Bad postcode", 200)
  //        }
  //      }
  //    }
  //    val service = new LocateService(new FakeApiClient, new JsonSerialiser, new MockConfig)
  //
  //    val mockedAddressService = new AddressService(service)//mock[AddressService]
  //
  //    val mockedIerApiService = mock[ConcreteIerApiService]
  //    val mockedJsonSerialiser = mock[JsonSerialiser]
  //    val mockedConfig = mock[Config]
  //    val mockedEncryptionService = mock[EncryptionService]
  //    val mockedRemoteAssets = mock[RemoteAssets]
  //
  //    val localAuthorityController = new LocalAuthorityController(
  //      mockedIerApiService,
  //      mockedAddressService,
  //      mockedJsonSerialiser,
  //      mockedEncryptionService,
  //      mockedConfig,
  //      mockedRemoteAssets
  //    )
  //
  ////    val result = localAuthorityController.doLookup(Some(postcode))(FakeRequest(POST, "/register-to-vote/local-authority/lookup"))
  //        val Some(result) = route(
  //      FakeRequest(POST, "/register-to-vote/local-authority/lookup")
  //      .withIerSession()
  //      .withFormUrlEncodedBody("postcode" -> postcode)
  //    )
  //    println(result)
  //    status(result) should be(SEE_OTHER)
  //    redirectLocation(result).get should be("register-to-vote/local-authority/" + gssCode)
  //    }
  //  }
}