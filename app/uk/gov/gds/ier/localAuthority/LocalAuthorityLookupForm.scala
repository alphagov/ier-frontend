package uk.gov.gds.ier.localAuthority

import uk.gov.gds.ier.validation.FormKeys
import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.data.Forms._
import uk.gov.gds.ier.validation.PostcodeValidator
import play.api.data.validation.{Invalid, Valid, Constraint}

trait LocalAuthorityLookupForm {
  self: FormKeys =>

  lazy val localAuthorityLookupForm = ErrorTransformForm(
    mapping(
      keys.postcode.key -> nonEmptyText
    )
    (LocalAuthorityRequest.apply)(LocalAuthorityRequest.unapply)
    .verifying(isPostcodeValid)
  )

  lazy val isPostcodeValid = Constraint[LocalAuthorityRequest](keys.postcode.key) {
    localAuthorityRequest =>
    PostcodeValidator.isValid(localAuthorityRequest.postcode) match {
      case true => Valid
      case _ => Invalid("ordinary_address_error_postcodeIsNotValid", keys.postcode)
    }
  }

}

