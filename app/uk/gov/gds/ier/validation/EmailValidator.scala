package uk.gov.gds.ier.validation

object EmailValidator {

  // See: http://www.regular-expressions.info/email.html
  val emailRegex =  """[A-Za-z0-9._%+'-]+@(?:[A-Za-z0-9-]+\.)+[A-Za-z]{2,}"""

  def isValid(email: Option[String]):Boolean = {
    email match {
      case Some(str) => isValid(str)
      case None => false
    }
  }

  def isValid(email: String) = {
    email.matches(emailRegex)
  }
}
