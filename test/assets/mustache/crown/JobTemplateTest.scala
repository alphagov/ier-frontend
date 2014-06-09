package assets.mustache.crown

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.transaction.crown.job.JobMustache
import uk.gov.gds.ier.test._

class JobTemplateTest
  extends FlatSpec
  with JobMustache
  with StepMustache
  with WithMockConfig
  with WithMockRemoteAssets
  with Matchers {

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {
      val data = new JobModel(
        question = Question(),
        jobTitle = Field(
          id = "jobTitleId",
          name = "jobTitleName",
          classes = "jobTitleClass",
          value = "jobTitleValue"
        ),
        govDepartment = Field(
          id = "govDepartmentId",
          name = "govDepartmentName",
          classes = "govDepartmentClass",
          value = "govDepartmentValue"
        )
      )

      val html = Mustache.render("crown/job", data)
      val doc = Jsoup.parse(html.toString)

      val jobTitleLabel = doc.select("label[for=jobTitleId]").first()
      jobTitleLabel should not be(null)
      jobTitleLabel.attr("for") should be("jobTitleId")

      val jobTitleDiv = doc.select("div[class*=jobTitleClass]").first()
      jobTitleDiv.attr("class") should include("jobTitleClass")
      val jobTitleInput = jobTitleDiv.select("input").first()
      jobTitleInput.attr("id") should be("jobTitleId")
      jobTitleInput.attr("name") should be("jobTitleName")
      jobTitleInput.attr("value") should be("jobTitleValue")
      jobTitleInput.attr("class") should include("jobTitleClass")


      val govDepartmentLabel = doc.select("label[for=govDepartmentId]").first()
      govDepartmentLabel should not be(null)
      govDepartmentLabel.attr("for") should be("govDepartmentId")

      val govDepartmentDiv = doc.select("div[class*=govDepartmentClass]").first()
      govDepartmentDiv.attr("class") should include("govDepartmentClass")
      val govDepartmentInput = govDepartmentDiv.select("input").first()
      govDepartmentInput.attr("id") should be("govDepartmentId")
      govDepartmentInput.attr("name") should be("govDepartmentName")
      govDepartmentInput.attr("value") should be("govDepartmentValue")
      govDepartmentInput.attr("class") should include("govDepartmentClass")

    }
  }
}
