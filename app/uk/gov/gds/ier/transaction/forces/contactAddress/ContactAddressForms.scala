package uk.gov.gds.ier.transaction.forces.contactAddress

import play.api.data.Forms._
import uk.gov.gds.ier.validation.{ ErrorTransformForm, ErrorMessages, FormKeys }
import uk.gov.gds.ier.model.{PossibleContactAddresses, ContactAddress, InprogressForces}
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import play.api.data.validation.{Invalid, Valid, Constraint}

trait ContactAddressForms extends ContactAddressConstraints {
    self: FormKeys with ErrorMessages =>

  private lazy val contactAddressMapping = mapping(
    keys.country.key -> optional(nonEmptyText),
    keys.postcode.key -> optional(nonEmptyText),
    keys.addressLine1.key -> optional(nonEmptyText),
    keys.addressLine2.key -> optional(nonEmptyText),
    keys.addressLine3.key -> optional(nonEmptyText),
    keys.addressLine4.key -> optional(nonEmptyText),
    keys.addressLine5.key -> optional(nonEmptyText)
  ) (
    ContactAddress.apply
  ) (
    ContactAddress.unapply
  )

  lazy val possibleContactAddressesMapping = mapping (
    keys.contactAddressType.key -> required(text, "Please answer this question"),
    keys.bfpoContactAddress.key -> optional(contactAddressMapping),
    keys.otherContactAddress.key -> optional(contactAddressMapping)

  ) (
    PossibleContactAddresses.apply
  ) (
    PossibleContactAddresses.unapply
  )

  val contactAddressForm = ErrorTransformForm(
    mapping(
        keys.contactAddress.key -> optional(possibleContactAddressesMapping) //.verifying (countryRequired, addressDetailsRequired)
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

  lazy val contactAddressRequired = Constraint[PossibleContactAddresses](keys.contactAddress.key) {
    contactAddress =>

      contactAddress match {
        case Some(PossibleContactAddresses(contactAddressType,bfpoContactAddress,otherContactAddress)) => contactAddressType match {
          case "bfpo" =>  bfpoContactAddress match {
            case Some(contactAddress) =>
              if (!contactAddress.addressLine1.getOrElse("").trim.isEmpty ||
                  !contactAddress.addressLine2.getOrElse("").trim.isEmpty ||
                  !contactAddress.addressLine3.getOrElse("").trim.isEmpty ||
                  !contactAddress.addressLine4.getOrElse("").trim.isEmpty ||
                  !contactAddress.addressLine5.getOrElse("").trim.isEmpty) => Valid

            case None =>
          }
          case "other" =>
          case _ =>
        }
        case None =>
      }
      Valid
  }

//    lazy val countryRequired = Constraint[Option[ContactAddress]](keys.contactAddress.key) {
//        optAddress =>
//            optAddress match {
//                case Some(address) if (!address.country.getOrElse("").trim.isEmpty) => Valid
//                case _ => Invalid("Please enter your country", keys.contactAddress.country)
//            }
//    }

    lazy val addressDetailsRequired = Constraint[Option[ContactAddress]](keys.contactAddress.key) {
        optAddress =>
            optAddress match {
                case Some(address) if (!address.addressLine1.getOrElse("").trim.isEmpty ||
                                       !address.addressLine2.getOrElse("").trim.isEmpty ||
                                       !address.addressLine3.getOrElse("").trim.isEmpty ||
                                       !address.addressLine4.getOrElse("").trim.isEmpty ||
                                       !address.addressLine5.getOrElse("").trim.isEmpty) => Valid
                case _ => Invalid("Please enter your address", keys.contactAddress.addressLine1)
            }
    }
}