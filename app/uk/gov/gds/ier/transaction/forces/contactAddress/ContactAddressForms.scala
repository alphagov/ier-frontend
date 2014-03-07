package uk.gov.gds.ier.transaction.forces.contactAddress

import play.api.data.Forms._
import uk.gov.gds.ier.validation.{Key, ErrorTransformForm, ErrorMessages, FormKeys}
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
  ) verifying contactAddressRequired

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
        case Some(PossibleContactAddresses(contactAddressType,bfpoContactAddress,otherContactAddress)) =>
          contactAddressType match {
            case "bfpo" => validateBFPOAddressRequired (bfpoContactAddress)
            case "other" => validateOtherAddressRequired (bfpoContactAddress)
            case _ => Valid
          }
        case None => throw new IllegalArgumentException
      }
  }

    def validateBFPOAddressRequired (bfpoContactAddress: Option[ContactAddress]) = {
      bfpoContactAddress match {
        case Some(contactAddress) => {
          val errorKeys = List(
            if (contactAddress.addressLine1.getOrElse("").trim.isEmpty &&
                contactAddress.addressLine2.getOrElse("").trim.isEmpty &&
                contactAddress.addressLine3.getOrElse("").trim.isEmpty &&
                contactAddress.addressLine4.getOrElse("").trim.isEmpty &&
                contactAddress.addressLine5.getOrElse("").trim.isEmpty)
              Some(keys.contactAddress.bfpoContactAddress.addressLine1) else None,
           if (contactAddress.postcode.getOrElse("").trim.isEmpty)
             Some(keys.contactAddress.bfpoContactAddress.postcode) else None
          ).flatten

          if (errorKeys.size == 0) {
            Valid
          } else {
            Invalid ("Please enter the address", errorKeys:_*)
          }
        }

        case None =>  Invalid (
          "Please enter the address",
          keys.contactAddress.bfpoContactAddress.addressLine1,
          keys.contactAddress.bfpoContactAddress.postcode)
      }
    }

  def validateOtherAddressRequired (otherContactAddress: Option[ContactAddress]) = {
    otherContactAddress match {
      case Some(contactAddress) => {
        val errorKeys = List(
          if (contactAddress.addressLine1.getOrElse("").trim.isEmpty &&
            contactAddress.addressLine2.getOrElse("").trim.isEmpty &&
            contactAddress.addressLine3.getOrElse("").trim.isEmpty &&
            contactAddress.addressLine4.getOrElse("").trim.isEmpty &&
            contactAddress.addressLine5.getOrElse("").trim.isEmpty)
            Some(keys.contactAddress.otherContactAddress.addressLine1) else None,
          if (contactAddress.postcode.getOrElse("").trim.isEmpty)
            Some(keys.contactAddress.otherContactAddress.postcode) else None,
          if (contactAddress.country.getOrElse("").trim.isEmpty)
            Some(keys.contactAddress.otherContactAddress.country) else None
        ).flatten

        if (errorKeys.size == 0) {
          Valid
        } else {
          Invalid ("Please enter the address", errorKeys:_*)
        }
      }

      case None =>  Invalid (
        "Please enter the address",
        keys.contactAddress.otherContactAddress.addressLine1,
        keys.contactAddress.otherContactAddress.postcode,
        keys.contactAddress.otherContactAddress.country)
    }
  }
}