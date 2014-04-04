package uk.gov.gds.ier.transaction.crown.address

import play.api.data.Forms._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.validation.{
  ErrorTransformForm,
  ErrorMessages,
  FormKeys,
  PostcodeValidator}
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.model.Addresses
import uk.gov.gds.ier.model.PartialAddress
import uk.gov.gds.ier.model.PossibleAddress
import scala.Some
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait AddressForms extends AddressConstraints {
  self: FormKeys
  with ErrorMessages
  with WithSerialiser =>

  // address mapping for select address page - the address part
  lazy val partialAddressMapping = mapping(
    keys.addressLine.key -> optional(nonEmptyText),
    keys.uprn.key -> optional(nonEmptyText),
    keys.postcode.key -> nonEmptyText,
    keys.manualAddress.key -> optional(manualPartialAddressLinesMapping)
  ) (
    PartialAddress.apply
  ) (
    PartialAddress.unapply
  ).verifying(postcodeIsValidPartialAddress, uprnOrManualDefined)

  // address mapping for manual address - the address individual lines part
  lazy val manualPartialAddressLinesMapping = mapping(
    keys.lineOne.key -> optional(nonEmptyText),
    keys.lineTwo.key -> optional(text),
    keys.lineThree.key -> optional(text),
    keys.city.key -> optional(nonEmptyText)
  ) (
    PartialManualAddress.apply
  ) (
    PartialManualAddress.unapply
  ).verifying(lineOneIsRequired, cityIsRequired)

  lazy val lastUkAddressMapping = mapping(
    keys.hasUkAddress.key -> optional(boolean),
    keys.address.key -> optional(partialAddressMapping)
  ) (
    LastUkAddress.apply
  ) (
    LastUkAddress.unapply
  )

  // address mapping for manual address - the address parent wrapper part
  lazy val manualPartialAddressMapping = mapping(
    keys.postcode.key -> nonEmptyText,
    keys.manualAddress.key -> optional(manualPartialAddressLinesMapping)
  ) (
    (postcode, manualAddress) => PartialAddress(
      addressLine = None,
      uprn = None,
      postcode = postcode,
      manualAddress = manualAddress
    )
  ) (
    partial => Some(
      partial.postcode,
      partial.manualAddress
    )
  ).verifying(postcodeIsValidPartialAddress)



  lazy val postcodeLookupMapping = mapping(
    keys.postcode.key -> nonEmptyText
  ) (
    postcode => PartialAddress(
        addressLine = None,
        uprn = None,
        postcode = postcode,
        manualAddress = None
      )
  ) (
    partial => Some(partial.postcode)
  ).verifying(postcodeIsValidPartialAddress)

  lazy val possibleAddressesMapping = mapping(
    keys.jsonList.key -> nonEmptyText,
    keys.postcode.key -> nonEmptyText
  ) (
    (json, postcode) => PossibleAddress(
      serialiser.fromJson[Addresses](json),
      postcode
    )
  ) (
    possibleAddress => Some(
      serialiser.toJson(possibleAddress.jsonList),
      possibleAddress.postcode
    )
  )

  val lookupAddressForm = ErrorTransformForm(
    mapping (
      keys.hasUkAddress.key -> optional(boolean),
      keys.address.key -> optional(postcodeLookupMapping)
    ) (
      (hasUkAddress, addr) => InprogressCrown(
        address = Some(LastUkAddress(hasUkAddress, addr))
      )
    ) (
      inprogress => Some(
        inprogress.address.get.hasUkAddress,
        inprogress.address.get.address
      )
    ).verifying( postcodeIsNotEmpty )
  )

  val addressForm = ErrorTransformForm(
    mapping (
      keys.hasUkAddress.key -> optional(boolean),
      keys.address.key -> optional(partialAddressMapping),
      keys.possibleAddresses.key -> optional(possibleAddressesMapping)
    ) (
      (hasUkAddress, addr, possibleAddr) => InprogressCrown(
        address = Some(LastUkAddress(hasUkAddress, addr)),
        possibleAddresses = possibleAddr
      )
    ) (
      inprogress => Some(
        if (inprogress.address.isDefined)
          inprogress.address.get.hasUkAddress
        else None,
        if (inprogress.address.isDefined)
          inprogress.address.get.address
        else None,
        inprogress.possibleAddresses
      )
    ).verifying( addressIsRequired )
  )

  val manualAddressForm = ErrorTransformForm(
    mapping(
      keys.hasUkAddress.key -> optional(boolean),
      keys.address.key -> optional(manualPartialAddressMapping)
    ) (
      (hasUkAddress, addr) => InprogressCrown(
        address = Some(LastUkAddress(hasUkAddress, addr))
      )
    ) (
      inprogress => Some(
        inprogress.address.get.hasUkAddress,
        inprogress.address.get.address
      )
    ).verifying( manualAddressIsRequired )
  )
}


trait AddressConstraints extends CommonConstraints {
  self: FormKeys
   with ErrorMessages =>

  lazy val manualAddressIsRequired = Constraint[InprogressCrown](keys.address.key) {
    inprogress =>
      inprogress.address match {
        case Some(lastUkAddress) => {
          if (lastUkAddress.address.isDefined) {
            val manualAddress = lastUkAddress.address.get.manualAddress
            if (manualAddress.isDefined) Valid
            else Invalid("Please answer this question", keys.address.manualAddress)
          }
          else Invalid("Please answer this question", keys.address.manualAddress)
        }
        case None => Invalid("Please answer this question", keys.address.manualAddress)
      }
  }

  lazy val postcodeIsNotEmpty = Constraint[InprogressCrown](keys.address.key) {
    inprogress =>
      inprogress.address match {
        case Some(lastUkAddress) => {
          if (lastUkAddress.address.isDefined) {
            val postcode = lastUkAddress.address.get.postcode
            if (postcode == "") Invalid("Please enter your postcode", keys.address.postcode)
            else Valid
          }
          else Invalid("Please enter your postcode", keys.address.postcode)
        }
        case None => Invalid("Please enter your postcode", keys.address.postcode)
      }
  }

  lazy val addressIsRequired = Constraint[InprogressCrown](keys.address.key) {
    inprogress =>
      if (inprogress.address.isDefined) {
        val lastUkAddress = inprogress.address.get
        if (lastUkAddress.address.isDefined) Valid
        else Invalid("Please answer this question", keys.address)
      }
      else Invalid("Please answer this question", keys.address)
  }

  lazy val uprnOrManualDefined = Constraint[PartialAddress](keys.address.key) {
    case partialAddress if partialAddress.uprn.exists(_ != "") => Valid
    case partialAddress if partialAddress.manualAddress.exists(_ != "") => Valid
    case _ => Invalid(
      "Please select your address",
      keys.address.uprn,
      keys.address.manualAddress,
      keys.address
    )
  }

  lazy val postcodeIsValidPartialAddress = Constraint[PartialAddress](keys.address.key) {
    case PartialAddress(_, _, postcode, _)
      if PostcodeValidator.isValid(postcode) => Valid
    case _ => Invalid("Your postcode is not valid", keys.address.postcode)
  }

  lazy val postcodeIsValid = Constraint[LastUkAddress](keys.address.key) {
    case LastUkAddress(_,Some(PartialAddress(_, _, postcode, _)))
      if PostcodeValidator.isValid(postcode) => Valid
    case _ => Invalid("Your postcode is not valid", keys.address.postcode)
  }

  lazy val lineOneIsRequired = Constraint[PartialManualAddress](
    keys.address.manualAddress.key) {
    case PartialManualAddress(Some(_), _, _, _) => Valid
    case _ => Invalid(lineOneIsRequiredError, keys.address.manualAddress.lineOne)
  }

  lazy val cityIsRequired = Constraint[PartialManualAddress](
    keys.address.manualAddress.key) {
    case PartialManualAddress(_, _, _, Some(_)) => Valid
    case _ => Invalid(cityIsRequiredError, keys.address.manualAddress.city)
  }
}

