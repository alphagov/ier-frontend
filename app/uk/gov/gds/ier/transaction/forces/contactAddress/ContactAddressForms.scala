package uk.gov.gds.ier.transaction.forces.contactAddress

import play.api.data.Forms._
import uk.gov.gds.ier.validation.{ ErrorTransformForm, ErrorMessages, FormKeys }
import uk.gov.gds.ier.model.{ContactAddress, InprogressForces}
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import play.api.data.validation.{Constraint, Valid, Invalid}

trait ContactAddressForms extends ContactAddressConstraints {
    self: FormKeys with ErrorMessages =>

    lazy val contactAddressMapping = mapping (
            keys.country.key -> optional(nonEmptyText),
            keys.postcode.key -> optional(nonEmptyText),
            keys.addressLine1.key -> optional(nonEmptyText),
            keys.addressLine2.key -> optional(nonEmptyText),
            keys.addressLine3.key -> optional(nonEmptyText),
            keys.addressLine4.key -> optional(nonEmptyText),
            keys.addressLine5.key -> optional(nonEmptyText),
            keys.addressType.key -> optional(nonEmptyText),
            keys.ukAddressLine.key -> optional(nonEmptyText)

  ) (ContactAddress.apply) (ContactAddress.unapply)
    
    val contactAddressForm = ErrorTransformForm(
        mapping(
          keys.contactAddress.key -> optional(contactAddressMapping) //.verifying (countryRequired, addressDetailsRequired)
    )(
          contactAddress => InprogressForces(contactAddress = contactAddress)
        )(
          inprogressForces => Some(inprogressForces.contactAddress)
        )
    ) 
}

trait ContactAddressConstraints extends CommonConstraints {
    self: FormKeys
    with ErrorMessages => 
        
//    lazy val countryRequired = Constraint[Option[ContactAddress]](keys.contactAddress.key) {
//        optAddress =>
//            optAddress match {
//                case Some(address) if (!address.country.getOrElse("").trim.isEmpty) => Valid
//                case _ => Invalid("Please enter your country", keys.contactAddress.country)
//            }
//    }
//    lazy val addressDetailsRequired = Constraint[Option[ContactAddress]](keys.contactAddress.key) {
//        optAddress =>
//            optAddress match {
//                case Some(address) if (!address.addressLine1.getOrElse("").trim.isEmpty ||
//                                       !address.addressLine2.getOrElse("").trim.isEmpty ||
//                                       !address.addressLine3.getOrElse("").trim.isEmpty ||
//                                       !address.addressLine4.getOrElse("").trim.isEmpty ||
//                                       !address.addressLine5.getOrElse("").trim.isEmpty) => Valid
//                case _ => Invalid("Please enter your address", keys.contactAddress.addressLine1)
//            }
//    }
}