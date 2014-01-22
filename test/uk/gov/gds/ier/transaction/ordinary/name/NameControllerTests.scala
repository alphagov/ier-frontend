package uk.gov.gds.ier.transaction.ordinary.name

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.{PreviousName, Name, InprogressOrdinary}

class NameControllerTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "NameController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/name").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 4")
      contentAsString(result) should include("<a class=\"back-to-previous\" href=\"/register-to-vote/date-of-birth")
      contentAsString(result) should include("What is your full name?")
      contentAsString(result) should include("Have you changed your name in the last 12 months?")
      contentAsString(result) should include("<form action=\"/register-to-vote/name\"")
    }
  }

  behavior of "NameController.post"
  it should "bind successfully and redirect to the Previous Name step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/name")
          .withIerSession()
          .withFormUrlEncodedBody(
            "name.firstName" -> "John",
            "name.lastName" -> "Smith",
            "previousName.hasPreviousName" -> "true",
            "previousName.previousName.firstName" -> "John",
            "previousName.previousName.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/nino"))
    }
  }

  it should "bind successfully with no previous name and redirect to Nino step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/name")
          .withIerSession()
          .withFormUrlEncodedBody(
            "name.firstName" -> "John",
            "name.lastName" -> "Smith",
            "previousName.hasPreviousName" -> "false")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/nino"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with a complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/name")
          .withIerSession()
          .withApplication(completeOrdinaryApplication)
          .withFormUrlEncodedBody(
            "name.firstName" -> "John",
            "name.lastName" -> "Smith",
            "previousName.hasPreviousName" -> "true",
            "previousName.previousName.firstName" -> "John",
            "previousName.previousName.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/name").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your full name?")
      contentAsString(result) should include("Please enter your full name")
      contentAsString(result) should include("Have you changed your name in the last 12 months?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("<form action=\"/register-to-vote/name\"")
    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withApplication(completeOrdinaryApplication.copy(name = None))
          .withFormUrlEncodedBody(
          "country.residence" -> "England"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/name"))
    }
  }

  behavior of "NameController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/edit/name").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 4")
      contentAsString(result) should include("<a class=\"back-to-previous\" href=\"/register-to-vote/confirmation")
      contentAsString(result) should include("What is your full name?")
      contentAsString(result) should include("Have you changed your name in the last 12 months?")
      contentAsString(result) should include("<form action=\"/register-to-vote/edit/name\"")
    }
  }

  behavior of "NameController.editPost"
  it should "bind successfully and redirect to the Previous Name step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/name")
          .withIerSession()
          .withFormUrlEncodedBody(
            "name.firstName" -> "John",
            "name.lastName" -> "Smith",
            "previousName.hasPreviousName" -> "true",
            "previousName.previousName.firstName" -> "John",
            "previousName.previousName.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/nino"))
    }
  }

  it should "bind successfully with no previous name and redirect to Nino step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/name")
          .withIerSession()
          .withFormUrlEncodedBody(
            "name.firstName" -> "John",
            "name.lastName" -> "Smith",
            "previousName.hasPreviousName" -> "false")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/nino"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with a complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/name")
          .withIerSession()
          .withApplication(completeOrdinaryApplication)
          .withFormUrlEncodedBody(
            "name.firstName" -> "John",
            "name.lastName" -> "Smith",
            "previousName.hasPreviousName" -> "true",
            "previousName.previousName.firstName" -> "John",
            "previousName.previousName.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/name").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your full name?")
      contentAsString(result) should include("Please enter your full name")
      contentAsString(result) should include("Have you changed your name in the last 12 months?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("<form action=\"/register-to-vote/edit/name\"")
    }
  }

  behavior of "access 'name page' with name already filled and no previous name filled"
  it should "display the page with name filled and previous name radio button set to false" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/name")
          .withIerSession(3)
          .withApplication(InprogressOrdinary(
          name = Some(Name(
            firstName = "John",
            middleNames = Some("Archibald William"),
            lastName= "Smith"
          ))))
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 4")

      contentAsString(result) should include("" +
        "<input type=\"text\" id=\"name_firstName\" name=\"name.firstName\" value=\"John\"")

      contentAsString(result) should include("" +
        "<input type=\"text\" id=\"name_middleNames\" name=\"name.middleNames\" value=\"Archibald William\"")

      contentAsString(result) should include("" +
        "<input type=\"text\" id=\"name_lastName\" name=\"name.lastName\" value=\"Smith\"")

      contentAsString(result) should include("" +
        "<input type=\"radio\" id=\"previousName_hasPreviousName_true\" name=\"previousName.hasPreviousName\"\n" +
        "                   value=\"true\" class=\"radio validate\" data-validation-name=\"previousYes\" data-validation-type=\"field\"\n" +
        "                   data-validation-rules=\"nonEmpty\" >")

      contentAsString(result) should include("" +
        "<input type=\"radio\" id=\"previousName_hasPreviousName_false\" name=\"previousName.hasPreviousName\"\n" +
        "                   value=\"false\"  class=\"radio  validate\"   data-validation-name=\"previousNo\"  data-validation-type=\"field\"\n" +
        "                   data-validation-rules=\"nonEmpty\" >")

      contentAsString(result) should include("" +
        "<input type=\"text\" id=\"previousName_previousName_firstName\" name=\"previousName.previousName.firstName\" value=\"\"")

      contentAsString(result) should include("" +
        "<input type=\"text\" id=\"previousName_previousName_middleNames\" name=\"previousName.previousName.middleNames\" value=\"\"")

      contentAsString(result) should include("" +
        "<input type=\"text\" id=\"previousName_previousName_lastName\" name=\"previousName.previousName.lastName\" value=\"\"")
    }
  }

  behavior of "access 'name page' with name already filled and also previous name filled"
  it should "display the page with name printed and previous name radio button set to true and previous name printed" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/name")
          .withIerSession(3)
          .withApplication(InprogressOrdinary(
          name = Some(Name(
            firstName = "John",
            middleNames = Some("Archibald William"),
            lastName= "Smith"
          )),
          previousName = Some(PreviousName(
            hasPreviousName = true,
            previousName = Some(Name(
              firstName = "Jan",
              middleNames = Some("Johannes"),
              lastName= "Kovar"
            ))
          ))
        ))
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 4")

      contentAsString(result) should include("" +
        "<input type=\"text\" id=\"name_firstName\" name=\"name.firstName\" value=\"John\"")

      contentAsString(result) should include("" +
        "<input type=\"text\" id=\"name_middleNames\" name=\"name.middleNames\" value=\"Archibald William\"")

      contentAsString(result) should include("" +
        "<input type=\"text\" id=\"name_lastName\" name=\"name.lastName\" value=\"Smith\"")

      contentAsString(result) should include("" +
        "<input type=\"radio\" id=\"previousName_hasPreviousName_true\" name=\"previousName.hasPreviousName\"\n" +
        "                   value=\"true\" class=\"radio validate\" data-validation-name=\"previousYes\" data-validation-type=\"field\"\n" +
        "                   data-validation-rules=\"nonEmpty\" checked>")

      contentAsString(result) should include("" +
        "<input type=\"radio\" id=\"previousName_hasPreviousName_false\" name=\"previousName.hasPreviousName\"\n" +
        "                   value=\"false\"  class=\"radio  validate\"   data-validation-name=\"previousNo\"  data-validation-type=\"field\"\n" +
        "                   data-validation-rules=\"nonEmpty\" >")

      contentAsString(result) should include("" +
        "<input type=\"text\" id=\"previousName_previousName_firstName\" name=\"previousName.previousName.firstName\" value=\"Jan\"")

      contentAsString(result) should include("" +
        "<input type=\"text\" id=\"previousName_previousName_middleNames\" name=\"previousName.previousName.middleNames\" value=\"Johannes\"")

      contentAsString(result) should include("" +
        "<input type=\"text\" id=\"previousName_previousName_lastName\" name=\"previousName.previousName.lastName\" value=\"Kovar\"")
    }
  }

  behavior of "access 'name page' with previous name is set explicitly to false"
  it should "display the page where previous name radio button is explicitly set to false" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/name")
          .withIerSession(3)
          .withApplication(InprogressOrdinary(
            previousName = Some(PreviousName(
              hasPreviousName = false,
              previousName = None
            ))
        ))
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 4")

      contentAsString(result) should include("" +
        "<input type=\"radio\" id=\"previousName_hasPreviousName_true\" name=\"previousName.hasPreviousName\"\n" +
        "                   value=\"true\" class=\"radio validate\" data-validation-name=\"previousYes\" data-validation-type=\"field\"\n" +
        "                   data-validation-rules=\"nonEmpty\" >")

      contentAsString(result) should include("" +
        "<input type=\"radio\" id=\"previousName_hasPreviousName_false\" name=\"previousName.hasPreviousName\"\n" +
        "                   value=\"false\"  class=\"radio  validate\"   data-validation-name=\"previousNo\"  data-validation-type=\"field\"\n" +
        "                   data-validation-rules=\"nonEmpty\" checked>")
    }
  }
}
