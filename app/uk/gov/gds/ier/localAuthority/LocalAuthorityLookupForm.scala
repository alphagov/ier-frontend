package uk.gov.gds.ier.localAuthority

import uk.gov.gds.ier.validation.FormKeys
import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.data._
import play.api.data.Forms._
import uk.gov.gds.ier.validation.PostcodeValidator

trait LocalAuthorityLookupForm {
  self: FormKeys =>

  lazy val localAuthorityLookupForm = ErrorTransformForm(
    mapping(
      keys.postcode.key -> nonEmptyText.verifying(PostcodeValidator.isValid(_))
    )
    (LocalAuthorityRequest.apply)(LocalAuthorityRequest.unapply)
  )
}

