package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.client.ApiClient
import uk.gov.gds.ier.test.BrowserHelpers
import uk.gov.gds.ier.DynamicGlobal
import uk.gov.gds.ier.model.{Fail, Success, ApiResponse}
import uk.gov.gds.ier.config.Config
import play.api.libs.ws.WS

class IntegrationSpec extends Specification with BrowserHelpers {

  val stubGlobal = new DynamicGlobal {
    override def bindings = { binder =>
      binder.bind(classOf[Config]).to(classOf[MockConfig])
      binder.bind(classOf[ApiClient]).to(classOf[MockApiClient])
    }
  }

  "RegisterToVote landing page" should {
    "be accessible from within a browser" in {
      running(TestServer(3333, FakeApplication(withGlobal = Some(stubGlobal))), HTMLUNIT) { browser =>

        browser.goTo("http://localhost:3333/")

        browser.pageSource must contain("Register to Vote")

      }
    }
  }

  "register to vote page" should {
    "successfully process a valid application" in {
      running(TestServer(3333, FakeApplication(withGlobal = Some(stubGlobal))), HTMLUNIT) { implicit browser =>
        goTo("http://localhost:3333/register-to-vote")

        formText("#firstName", "John")
        formText("#middleName", "James")
        formText("#lastName", "Smith")
        formText("#previousLastName", "Jones")
        formText("#dob", "1988-01-01")
        formText("#nino", "AB 12 34 56 D")

        click("#submit")

        waitForSelector("#confirmation")
        browser.pageSource() must contain("First Name: John")
        browser.pageSource() must contain("Last Name: Smith")
        browser.pageSource() must contain("Previous Last Name: Jones")
        browser.pageSource() must contain("Date Of Birth: 1988-01-01")
        browser.pageSource() must contain("Middle Name: James")
        browser.pageSource() must contain("IER ID: 1234")
        browser.pageSource() must contain("Nino: AB 12 34 56 D")
        browser.pageSource() must contain("Creation Date: 1988-01-01 12:00:00")
        browser.pageSource() must contain("Status: Unprocessed")
      }
    }
  }

  "postcode lookup" should {
    "return an address for a good postcode" in {
      running(TestServer(3333, FakeApplication(withGlobal = Some(stubGlobal))), HTMLUNIT) { implicit browser =>
        val result = new ApiClient(new MockConfig).get("http://localhost:3333/address/BT125EG")
        result match {
          case Success(addresses) => {
            addresses must contain("\"addressLine\":\"Apartment 3/1, Block A, 181 Sandy Row, Belfast, County Antrim\",\"postcode\":\"BT12 5EG\"")
          }
          case Fail(error) => failure("Should not have failed " + error)
        }
      }
    }

    "return a bad request for bad postcode" in {
      running(TestServer(3333, FakeApplication(withGlobal = Some(stubGlobal))), HTMLUNIT) { implicit browser =>
        val result = new ApiClient(new MockConfig).get("http://localhost:3333/address/ZS1234BAD")
        result match {
          case Success(addresses) => {
            failure("Should not have succeeded")
          }
          case Fail(error) => {
            error must contain("postcode")
          }
        }
      }
    }
  }
}

class MockConfig extends Config {
  override def paKey = "1234"
  override def paUrl = "http://pa.com"
  override def apiTimeout = 1
}

class MockApiClient extends ApiClient(new MockConfig) {
  override def get(url:String) : ApiResponse = {
    if (url == "http://pa.com?key=1234&postcode=BT125EG") {
      Success("""{
          "Items": [
            {
              "Udprn": 51088262,
              "Company": "",
              "Department": "",
              "Line1": "Apartment 3/1",
              "Line2": "Block A",
              "Line3": "181 Sandy Row",
              "Line4": "",
              "Line5": "",
              "PostTown": "Belfast",
              "County": "County Antrim",
              "Postcode": "BT12 5EG",
              "Mailsort": 10121,
              "Barcode": "(BT125EG1B2)",
              "Type": "Residential",
              "DeliveryPointSuffix": "1B",
              "SubBuilding": "Apartment 3/1",
              "BuildingName": "Block A",
              "BuildingNumber": "181",
              "PrimaryStreet": "Sandy Row",
              "SecondaryStreet": "",
              "DoubleDependentLocality": "",
              "DependentLocality": "",
              "PoBox": "",
              "PrimaryStreetName": "Sandy",
              "PrimaryStreetType": "Row",
              "SecondaryStreetName": "",
              "SecondaryStreetType": "",
              "CountryName": "Northern Ireland"
            }]
          }""")
    } else {
      Fail("Bad postcode")
    }
  }
  override def post(url:String, content:String) : ApiResponse = {
    if (content contains """"fn":"John"""") {
      Success("""{
          "ierId" : "1234",
          "createdAt" : "1988-01-01 12:00:00",
          "status" : "Unprocessed",
          "source" : "web",
          "detail" : """ + content + "}")
    } else {
      Fail("Invalid firstName")
    }
  }
}