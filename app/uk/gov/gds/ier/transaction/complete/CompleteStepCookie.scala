package uk.gov.gds.ier.transaction.complete

import uk.gov.gds.ier.service.apiservice.EroAuthorityDetails
import uk.gov.gds.ier.step.InprogressApplication

case class CompleteStepCookie(
    refNum: String,
    authority: Option[EroAuthorityDetails],
    hasOtherAddress: Boolean,
    backToStartUrl: String,
    showEmailConfirmation: Boolean
  ) extends InprogressApplication[CompleteStepCookie] {

  def merge(other: CompleteStepCookie) = {
    // CompleteStepCookie does not need real merge, it is not filled per partes, it is filled once only
    other.copy(
      refNum = this.refNum,
      authority = this.authority,
      hasOtherAddress = this.hasOtherAddress,
      backToStartUrl = this.backToStartUrl
    )
  }
}

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
