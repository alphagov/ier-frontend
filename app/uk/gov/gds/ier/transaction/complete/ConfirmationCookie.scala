package uk.gov.gds.ier.transaction.complete

import uk.gov.gds.ier.service.apiservice.EroAuthorityDetails

case class ConfirmationCookie(
    refNum: String,
    authority: Option[EroAuthorityDetails],
    hasOtherAddress: Boolean = false,
    backToStartUrl: String,
    showEmailConfirmation: Boolean,
    showBirthdayBunting: Boolean
  )

object ConfirmationCookie {
  def apply(): ConfirmationCookie = {
    ConfirmationCookie(
      refNum = "",
      authority = None,
      hasOtherAddress = false,
      backToStartUrl = "",
      showEmailConfirmation = false,
      showBirthdayBunting = false
    )
  }
}
