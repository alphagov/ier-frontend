package uk.gov.gds.ier.step.dateOfBirth

import uk.gov.gds.ier.model.{InprogressApplication, DateOfBirth}
import uk.gov.gds.ier.validation._
import play.api.data.Form
import play.api.data.Forms._
import uk.gov.gds.ier.model.DateOfBirth
import uk.gov.gds.ier.model.InprogressApplication
import scala.Some
import uk.gov.gds.ier.validation.constraints.DateOfBirthConstraints

trait DateOfBirthForms extends DateOfBirthConstraints {
    self:  FormKeys
      with ErrorMessages =>

  lazy val dobMapping = mapping(
    keys.year.key -> text
      .verifying("Please enter your year of birth", _.nonEmpty)
      .verifying("The year you provided is invalid", year => year.isEmpty || year.matches("\\d+")),
    keys.month.key -> text
      .verifying("Please enter your month of birth", _.nonEmpty)
      .verifying("The month you provided is invalid", month => month.isEmpty || month.matches("\\d+")),
    keys.day.key -> text
      .verifying("Please enter your day of birth", _.nonEmpty)
      .verifying("The day you provided is invalid", day => day.isEmpty || day.matches("\\d+"))
  ) {
    (year, month, day) => DateOfBirth(year.toInt, month.toInt, day.toInt)
  } {
    dateOfBirth => Some(dateOfBirth.year.toString, dateOfBirth.month.toString, dateOfBirth.day.toString)
  }.verifying(isOverTheMinimumAgeToVote, dateNotInTheFuture, notTooOldToBeAlive)


  val dateOfBirthForm = ErrorTransformForm(
    mapping(keys.dob.key -> optional(dobMapping))
    (
      dob => InprogressApplication(dob = dob)
    )(
      inprogress => Some(inprogress.dob)
    ) verifying dateOfBirthRequired
  )
}
