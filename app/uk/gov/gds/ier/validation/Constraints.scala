package uk.gov.gds.ier.validation

import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model.Contact

trait Constraints {
  self: FormKeys =>

  lazy val contactTelephoneConstraint = Constraint[Contact](keys.contact.phone.key) { contactDetails =>
    if (contactDetails.contactMethod != "phone" || (contactDetails.contactMethod == "phone" && contactDetails.phone.isDefined)) Valid
    else Invalid("Please enter your phone number", keys.contact.phone)
  }
  lazy val contactTextConstraint = Constraint[Contact](keys.contact.textNum.key) { contactDetails =>
    if (contactDetails.contactMethod != "text" || (contactDetails.contactMethod == "text" && contactDetails.textNum.isDefined)) Valid
    else Invalid("Please enter your phone number", keys.contact.textNum)
  }
  lazy val contactEmailConstraint = Constraint[Contact](keys.contact.email.key) { contactDetails =>
    if (contactDetails.contactMethod != "email" || (contactDetails.contactMethod == "email" && contactDetails.email.isDefined)) Valid
    else Invalid("Please enter your email address", keys.contact.email)
  }
}
