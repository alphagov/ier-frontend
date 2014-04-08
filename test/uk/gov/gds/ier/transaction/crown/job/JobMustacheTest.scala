package uk.gov.gds.ier.transaction.crown.job

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.{Job}
import scala.Some
import controllers.step.crown.routes._
import uk.gov.gds.ier.transaction.crown.InprogressCrown

class JobMustacheTest
  extends FlatSpec
  with Matchers
  with JobForms
  with ErrorMessages
  with FormKeys
  with TestHelpers
  with JobMustache {

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = jobForm
    val emptyApplication = InprogressCrown()
    val jobModel = mustache.data(
      emptyApplicationForm,
      JobController.post,
      Some(NinoController.get),
      emptyApplication
    ).data.asInstanceOf[JobModel]

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

    val jobModel = mustache.data(
      partiallyFilledApplicationForm,
      JobController.post,
      Some(NinoController.get),
      partiallyFilledApplication
    ).data.asInstanceOf[JobModel]

    jobModel.question.title should be("What is your role?")
    jobModel.question.postUrl should be("/register-to-vote/crown/job-title")
    jobModel.question.backUrl should be("/register-to-vote/crown/nino")

    jobModel.jobTitle.value should be("Doctor")
    jobModel.govDepartment.value should be("Fake Dept")

  }
}
