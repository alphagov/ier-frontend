package uk.gov.gds.ier.validation

object PassportNumberValidator {

  val passportNumberRegex = "^[0-9]{9}$/"

  def isValid(passportNumber: String) = {
    passportNumber.replaceAll("\\s", "").matches(passportNumberRegex)
  }
}
