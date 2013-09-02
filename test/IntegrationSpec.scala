package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.client.ApiClient
import uk.gov.gds.ier.test.BrowserHelpers
import uk.gov.gds.ier.DynamicGlobal
import uk.gov.gds.ier.model.{Fail, Success, ApiResponse}
import uk.gov.gds.ier.config.Config

class IntegrationSpec extends Specification with BrowserHelpers {

  val stubGlobal = new DynamicGlobal {
    override def bindings = { binder =>
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
}

class MockConfig extends Config {
}

class MockApiClient extends ApiClient(new MockConfig) {
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