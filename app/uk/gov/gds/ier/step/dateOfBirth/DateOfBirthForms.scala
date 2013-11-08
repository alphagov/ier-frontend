package uk.gov.gds.ier.step.dateOfBirth

import uk.gov.gds.ier.model.{InprogressApplication, DateOfBirth}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import play.api.data.Form
import play.api.data.Forms._
import uk.gov.gds.ier.validation.DateValidator

trait DateOfBirthForms {
    self:  FormKeys
      with ErrorMessages =>

  val dobMapping = mapping(
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
  }.verifying("The date you specified is invalid", 
      dob => DateValidator.isExistingDateInThePast(dob) && !DateValidator.isTooOldToBeAlive(dob))
    .verifying(s"Minimum age to register to vote is ${DateValidator.minimumAge}", 
      dob => !DateValidator.isExistingDateInThePast(dob) || !DateValidator.isTooYoungToRegister(dob))

  val dateOfBirthForm = Form(
    mapping(keys.dob.key -> optional(dobMapping).verifying("Please enter your date of birth", _.isDefined))
      (dob => InprogressApplication(dob = dob))
      (inprogress => Some(inprogress.dob))
  )
}