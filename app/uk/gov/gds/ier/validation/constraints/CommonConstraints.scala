package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{ErrorMessages, Key}
import play.api.data.validation.{Invalid, Valid, Constraint}
import play.api.data.Mapping
import play.api.data.Forms._
import uk.gov.gds.ier.validation.Key

trait CommonConstraints extends ErrorMessages {

  def requiredOptional[A](mapping: Mapping[A], errorMessage:String):Mapping[Option[A]] = {
    optional(mapping).verifying(errorMessage, _.nonEmpty)
  }

  protected def fieldNotTooLong [T](fieldKey:Key, errorMessage:String)
                                   (fieldValue:T => String) = {
    Constraint[T](fieldKey.key) {
      t =>
        if (fieldValue(t).size <= maxTextFieldLength) Valid
        else Invalid(errorMessage, fieldKey)
    }
  }
}
