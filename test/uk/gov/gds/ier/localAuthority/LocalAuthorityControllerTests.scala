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
import uk.gov.gds.ier.model.ApiResponse
import uk.gov.gds.ier.model.Success
import uk.gov.gds.ier.model.Fail
import uk.gov.gds.ier.service.LocateService
import uk.gov.gds.ier.DynamicGlobal
import uk.gov.gds.ier.client.ApiClient
import uk.gov.gds.ier.guice.WithConfig
import uk.gov.gds.ier.client.IerApiClient

class LocalAuthorityControllerTests
  extends FlatSpec
  with Matchers
  with TestHelpers
  with MockitoSugar {

  val stubConfig = new Config {
	  override def locateUrl = "http://locateurl"
	  override def locateAuthorityUrl = "http://localAuthorityUrl"
	  override def locateApiAuthorizationToken = "token"
	  override def apiTimeout = 10
  }

  val mockApiClient = new LocateApiClient(stubConfig) {
    override def get(url: String, headers: (String, String)*) : ApiResponse = {
      val json = """
      {
	    "gssCode": "E09000030",
	    "contactDetails": {
	        "addressLine1": "address_line_1",
	        "postcode": "a11aa",
	        "emailAddress": "email@address.com",
	        "phoneNumber": "0123456789",
	        "name": "Tower Hamlets"
	    },
	    "eroIdentifier": "tower-hamlets",
	    "eroDescription": "Tower Hamlets"
      }
      """
      Success(json, 0)
    }
  }



  val stubGlobal = new DynamicGlobal {
    override def bindings = { binder =>
      binder bind classOf[Config] toInstance stubConfig
      binder bind classOf[ApiClient] toInstance mockApiClient
    }
  }

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

  behavior of "gss lookup"
    it should "redirect to the show local authority page" in {
	  running(FakeApplication(withGlobal = Some(stubGlobal))) {
    	val postcode = "ab123cd"
        val Some(result) = route(
    	  FakeRequest(POST, "/register-to-vote/local-authority/lookup")
    		.withIerSession()
    		.withFormUrlEncodedBody("postcode" -> postcode)
    	  )
      status(result) should be(SEE_OTHER)
      redirectLocation(result).get should be("/register-to-vote/local-authority/" + "E09000030")
    }
  }
}

