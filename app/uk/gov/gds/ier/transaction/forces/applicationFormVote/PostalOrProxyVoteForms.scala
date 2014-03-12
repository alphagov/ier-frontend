package uk.gov.gds.ier.transaction.forces.applicationFormVote

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model._
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.overseas.PostalOrProxyVoteConstraints
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model.PostalOrProxyVote
import uk.gov.gds.ier.model.PostalVoteDeliveryMethod
import uk.gov.gds.ier.model.InprogressForces
import scala.Some

trait PostalOrProxyVoteForms extends PostalOrProxyVoteForcesConstraints {
  self:  FormKeys
    with ErrorMessages =>

  lazy val voteDeliveryMethodMapping = mapping(
    keys.methodName.key -> optional(nonEmptyText),
    keys.emailAddress.key -> optional(nonEmptyText)
  )(
    PostalVoteDeliveryMethod.apply
  )(
    PostalVoteDeliveryMethod.unapply
  ) verifying (validDeliveryMethod)

  lazy val postalOrProxyVoteMapping = mapping(
    keys.voteType.key -> text.verifying("Unknown type", r => WaysToVoteType.isValid(r)),
    keys.optIn.key -> optional(boolean)
      .verifying("Please answer this question", postalVote => postalVote.isDefined),
    keys.deliveryMethod.key -> optional(voteDeliveryMethodMapping),
    keys.forceToRedirect.key -> boolean
  ) (
    (voteType, postalVoteOption, deliveryMethod, force) => PostalOrProxyVote(
    		WaysToVoteType.parse(voteType),
      postalVoteOption,
      deliveryMethod,
      force
    )
  ) (
    postalVote => Some(
      postalVote.typeVote.name,
      postalVote.postalVoteOption,
      postalVote.deliveryMethod,
      postalVote.forceRedirectToPostal
    )
  ) verifying (validVoteOption, forceMustBeFalse)

  val postalOrProxyVoteForm = ErrorTransformForm(
    mapping(
      keys.postalOrProxyVote.key -> optional(postalOrProxyVoteMapping)
    ) (
        postalVote => InprogressForces (postalOrProxyVote = postalVote)
    ) (
        inprogress => Some(inprogress.postalOrProxyVote)
    ) verifying questionIsRequiredForces
  )
}

trait PostalOrProxyVoteForcesConstraints extends PostalOrProxyVoteConstraints {
  self: ErrorMessages
    with FormKeys =>
      
  lazy val forceMustBeFalse = Constraint[PostalOrProxyVote](keys.forceToRedirect.key) {
    pvote => 
      if (!pvote.forceRedirectToPostal) Valid 
      else Invalid("", keys.forceToRedirect)
  }
      
  lazy val questionIsRequiredForces = Constraint[InprogressForces](keys.postalOrProxyVote.key) {
    application => application.postalOrProxyVote match {
      case Some(p) => Valid
      case None => Invalid("Please answer this question", keys.postalOrProxyVote.optIn)
    }
  }

}
