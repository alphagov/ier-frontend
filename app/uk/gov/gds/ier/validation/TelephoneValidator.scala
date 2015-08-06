package uk.gov.gds.ier.validation

object TelephoneValidator {

  val telephoneRegex = "^[0-9]{3,30}$"

  def isValid(telephone: String) = {
    telephone.replaceAll("[\\s\\+\\-\\_\\(\\)\\[A-Za-z]", "").matches(telephoneRegex)
  }
}
