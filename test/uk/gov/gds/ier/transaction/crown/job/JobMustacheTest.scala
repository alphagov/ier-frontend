package uk.gov.gds.ier.transaction.crown.job

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.{Job, InprogressCrown}
import scala.Some
import controllers.step.crown.routes._

class JobMustacheTest
  extends FlatSpec
  with Matchers
  with JobForms
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val jobMustache = new JobMustache {}

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = jobForm
    val emptyApplication = InprogressCrown()
    val jobModel = jobMustache.transformFormStepToMustacheData(
      emptyApplication,
      emptyApplicationForm,
      JobController.post,
      Some(NinoController.get))

    jobModel.question.title should be("What is your role?")
    jobModel.question.postUrl should be("/register-to-vote/crown/job-title")
    jobModel.question.backUrl should be("/register-to-vote/crown/nino")

    jobModel.jobTitle.value should be("")
    jobModel.govDepartment.value should be("")

  }

  it should "progress form with filled applicant name should produce Mustache Model with name values present" in {

    val partiallyFilledApplication = InprogressCrown(
      job = Some(Job(
        jobTitle = Some("Doctor"),
        govDepartment = Some("Fake Dept")
      ))
    )

    val partiallyFilledApplicationForm = jobForm.fill(partiallyFilledApplication)

    val jobModel = jobMustache.transformFormStepToMustacheData(
      partiallyFilledApplication,
      partiallyFilledApplicationForm,
      JobController.post,
      Some(NinoController.get))

    jobModel.question.title should be("What is your role?")
    jobModel.question.postUrl should be("/register-to-vote/crown/job-title")
    jobModel.question.backUrl should be("/register-to-vote/crown/nino")

    jobModel.jobTitle.value should be("Doctor")
    jobModel.govDepartment.value should be("Fake Dept")

  }
}
