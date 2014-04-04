package uk.gov.gds.ier.transaction.crown.job

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import play.api.data.Forms._
import uk.gov.gds.ier.model._
import scala.Some
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait JobForms extends JobConstraints {
  self:  FormKeys
    with ErrorMessages =>

  lazy val jobMapping = mapping(
    keys.jobTitle.key -> optional(nonEmptyText),
    keys.govDepartment.key -> optional(nonEmptyText)
  ) (
    (jobTitle, govDepartment) => Job(jobTitle, govDepartment)
  ) (
    job => Some(job.jobTitle, job.govDepartment)
  ) verifying  jobTitleAndGovDepartmentRequired

  val jobForm = ErrorTransformForm(
    mapping(
      keys.job.key -> optional(jobMapping)
    ) (
      job => InprogressCrown(job = job)
    ) (
      inprogressApplication => Some(inprogressApplication.job)
    ) verifying jobObjectRequired
  )
}
trait JobConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val jobObjectRequired = Constraint[InprogressCrown](keys.job.key) {
    application => application.job match {
      case Some(job) => Valid
      case None => Invalid(
        "Please answer this question",
        keys.job.jobTitle,
        keys.job.govDepartment
      )
    }
  }

  lazy val jobTitleAndGovDepartmentRequired = Constraint[Job](keys.job.key) {
    job => job match {
      case Job(Some(jobTitle), None) =>
        Invalid("Please answer this question",keys.job.govDepartment)
      case Job(None, Some(govDepartment)) =>
        Invalid("Please answer this question",keys.job.jobTitle)
      case _ => Valid
    }
  }
}
