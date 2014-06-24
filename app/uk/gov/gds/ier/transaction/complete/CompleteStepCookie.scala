package uk.gov.gds.ier.transaction.complete

import uk.gov.gds.ier.service.apiservice.EroAuthorityDetails

case class CompleteStepCookie(
    refNum: String,
    authority: Option[EroAuthorityDetails],
    hasOtherAddress: Boolean,
    backToStartUrl: String,
    showEmailConfirmation: Boolean
  )

object CompleteStepCookie {
  def apply(): CompleteStepCookie = {
    CompleteStepCookie(
      refNum = "",
      authority = None,
      hasOtherAddress = false,
      backToStartUrl = "",
      showEmailConfirmation = false
    )
  }
}
