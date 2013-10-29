package uk.gov.gds.ier.validation

object EmailValidator {

  val emailRegex = """\w+@\w+?(?:\.[A-Za-z]{2,3})+"""

  def isValid(email:Option[String]):Boolean = {
    email match {
      case Some(str) => isValid(str)
      case None => false
    }
  }

  def isValid(email:String) = {
    email.matches(emailRegex)
  }
}
