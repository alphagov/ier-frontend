package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{Key, FormKeys, ErrorMessages}
import uk.gov.gds.ier.model.ContactDetail

trait ContactConstraints extends CommonConstraints {
  self:  FormKeys
  with ErrorMessages =>

  def detailFilled(key:Key, name:String) = {
    predicateHolds[ContactDetail](key, s"Please enter your $name") {
      t => t.detail.isDefined || !t.contactMe
    }
  }
}
