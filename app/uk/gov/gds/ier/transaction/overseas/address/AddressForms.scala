package uk.gov.gds.ier.transaction.overseas.address

import play.api.data.Forms._
import uk.gov.gds.ier.validation.{ ErrorTransformForm, ErrorMessages, FormKeys }
import uk.gov.gds.ier.model.{ InprogressOverseas, OverseasAddress }
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import play.api.data.validation.{Constraint, Valid, Invalid}

trait AddressForms {//extends OverseasAddressConstraints {
    self: FormKeys with ErrorMessages =>

    lazy val addressMapping = mapping(
            keys.country.key -> optional(text).verifying("Correspondence country is required", _.nonEmpty),
            keys.overseasAddressDetails.key -> optional(text).verifying("Corresponding address is required", _.nonEmpty)
            ) (OverseasAddress.apply) (OverseasAddress.unapply) 
    
    val addressForm = ErrorTransformForm(
        mapping(keys.overseasAddress.key -> optional(addressMapping))
        (overseasAddress => InprogressOverseas(address = overseasAddress)) (inprogressOverseas => Some(inprogressOverseas.address))
//        mapping(keys.overseasAddress.key -> optional(addressMapping).verifying (countryRequired, addressDetailsRequired))
    )
}

//trait OverseasAddressConstraints extends CommonConstraints {
//    self: FormKeys
//    with ErrorMessages => 
//        
//    lazy val countryRequired = Constraint[Option[OverseasAddress]](keys.overseasAddress.key) {
//        optAddress => 
//            optAddress match {
//                case Some(address) if (address.country.isDefined) => Valid 
//                case _ => Invalid("Please select the country", keys.overseasAddress.country)
//            }
//    }
//    lazy val addressDetailsRequired = Constraint[Option[OverseasAddress]](keys.overseasAddress.key) {
//        optAddress => 
//            optAddress match {
//                case Some(address) if (address.addressDetails.isDefined) => Valid 
//                case _ => Invalid("Please enter the address", keys.overseasAddress.overseasAddressDetails)
//            }
//    }
//}