package uk.gov.gds.ier.transaction.overseas.address

import play.api.data.Forms._
import uk.gov.gds.ier.validation.{ ErrorTransformForm, ErrorMessages, FormKeys }
import uk.gov.gds.ier.model.{ InprogressOverseas, OverseasAddress }
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import play.api.data.validation.{Constraint, Valid, Invalid}

trait AddressForms extends OverseasAddressConstraints {
    self: FormKeys with ErrorMessages =>

    lazy val addressMapping = mapping (
            keys.country.key -> optional(nonEmptyText),
            keys.overseasAddressDetails.key -> optional(nonEmptyText)
    ) (OverseasAddress.apply) (OverseasAddress.unapply) 
    
    val addressForm = ErrorTransformForm(
        mapping(keys.overseasAddress.key -> optional(addressMapping).verifying (countryRequired, addressDetailsRequired))
        (overseasAddress => InprogressOverseas(address = overseasAddress))(inprogressOverseas => Some(inprogressOverseas.address))
    ) 
}

trait OverseasAddressConstraints extends CommonConstraints {
    self: FormKeys
    with ErrorMessages => 
        
    lazy val countryRequired = Constraint[Option[OverseasAddress]](keys.overseasAddress.key) {
        optAddress => 
            optAddress match {
                case Some(address) if (!address.country.getOrElse("").trim.isEmpty) => Valid 
                case _ => Invalid("Correspondence country is required", keys.overseasAddress.country)
            }
    }
    lazy val addressDetailsRequired = Constraint[Option[OverseasAddress]](keys.overseasAddress.key) {
        optAddress => 
            optAddress match {
                case Some(address) if (!address.addressDetails.getOrElse("").trim.isEmpty) => Valid 
                case _ => Invalid("Correspondence address is required", keys.overseasAddress.overseasAddressDetails)
            }
    }
}