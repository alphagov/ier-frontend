package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{EmailValidator, Key, FormKeys, ErrorMessages}
import uk.gov.gds.ier.model.{Contact, ContactDetail}
import play.api.data.validation.{Invalid, Valid, Constraint}

trait ContactConstraints extends CommonConstraints {
  self:  FormKeys
  with ErrorMessages =>

  def detailFilled(key:Key, name:String) = {
    predicateHolds[ContactDetail](key, s"Please enter your $name") {
      t => t.detail.isDefined || !t.contactMe
    }
  }

  lazy val emailIsValid = Constraint[Contact](keys.contact.key) {
    contact =>
      contact.email match {
        case Some(ContactDetail(true, Some(emailAddress))) => {
          if (EmailValidator.isValid(emailAddress)) Valid
          else Invalid("Please enter a valid email address", keys.contact.email.detail)
        }
        case _ => Valid
      }
  }
}
