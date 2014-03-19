package assets.mustache.crown

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.transaction.crown.confirmation.ConfirmationMustache

class ConfirmationTemplateTests
  extends FlatSpec
  with ConfirmationMustache
  with StepMustache
  with Matchers {

  it should "not render the partners details block if displayPartnerBlock = false" in {
    running(FakeApplication()) {
      val model = ConfirmationModel(
        applicantDetails = List(
          ConfirmationQuestion(
            content = "Some applicant details",
            title = "Applicant Details",
            editLink = "http://applicantDetails",
            changeName = "applicant details"
          )
        ),
        partnerDetails = List(
          ConfirmationQuestion(
            content = "Some applicant details",
            title = "Applicant Details",
            editLink = "http://applicantDetails",
            changeName = "applicant details"
          )
        ),
        displayPartnerBlock = false,
        backUrl = "http://backUrl",
        postUrl = "http://postUrl"
      )

      val html = Mustache.render("crown/confirmation", model)
      val doc = Jsoup.parse(html.toString)

      doc.select("h2").size() should be(0)
      doc.html should not include("Your partner's details")
    }
  }


  it should "render the partners details block if displayPartnerBlock = true" in {
    running(FakeApplication()) {
      val model = ConfirmationModel(
        applicantDetails = List(
          ConfirmationQuestion(
            content = "Some applicant details",
            title = "Applicant Details",
            editLink = "http://applicantDetails",
            changeName = "applicant details"
          )
        ),
        partnerDetails = List(
          ConfirmationQuestion(
            content = "Some applicant details",
            title = "Applicant Details",
            editLink = "http://applicantDetails",
            changeName = "applicant details"
          )
        ),
        displayPartnerBlock = true,
        backUrl = "http://backUrl",
        postUrl = "http://postUrl"
      )

      val html = Mustache.render("crown/confirmation", model)
      val doc = Jsoup.parse(html.toString)

      val partnerH2 = doc.select("h2").first()
      partnerH2 should not be(null)
      partnerH2.html should be("Your partner's details")

      val applicantH2 = doc.select("h2").get(1)
      applicantH2 should not be(null)
      applicantH2.html should be("Your details")
    }
  }
}
