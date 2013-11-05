package uk.gov.gds.ier.validation

import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model.Contact

trait Constraints {
  self: FormKeys =>

  val contactTelephoneConstraint = Constraint[Contact](contact.phone.key) { contactDetails =>
    if (contactDetails.contactMethod != "phone" || (contactDetails.contactMethod == "phone" && contactDetails.phone.isDefined)) Valid
    else Invalid("Please enter your phone number", contact.phone)
  }
  val contactTextConstraint = Constraint[Contact](contact.textNum.key) { contactDetails =>
    if (contactDetails.contactMethod != "text" || (contactDetails.contactMethod == "text" && contactDetails.textNum.isDefined)) Valid
    else Invalid("Please enter your phone number", contact.textNum)
  }
  val contactEmailConstraint = Constraint[Contact](contact.email.key) { contactDetails =>
    if (contactDetails.contactMethod != "email" || (contactDetails.contactMethod == "email" && contactDetails.email.isDefined)) Valid
    else Invalid("Please enter your email address", contact.email)
  }
}
