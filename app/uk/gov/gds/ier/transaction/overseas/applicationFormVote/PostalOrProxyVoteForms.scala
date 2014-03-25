package uk.gov.gds.ier.transaction.overseas.applicationFormVote

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model._
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.overseas.PostalOrProxyVoteConstraints
import play.api.data.validation.{Invalid, Valid, Constraint}

trait PostalOrProxyVoteForms extends PostalOrProxyVoteOverseasConstraints {
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

  val forceRedirect = boolean.verifying( "fails if true", _ == false)
  
  lazy val postalOrProxyVoteMapping = mapping(
    keys.voteType.key -> text.verifying("Unknown type", r => WaysToVoteType.isValid(r)),
    keys.optIn.key -> optional(boolean)
      .verifying("Please answer this question", postalVote => postalVote.isDefined),
    keys.deliveryMethod.key -> optional(voteDeliveryMethodMapping),
    keys.forceToRedirect.key -> optional(forceRedirect)
  ) (
    (voteType, postalVoteOption, deliveryMethod, force) => PostalOrProxyVote(
      WaysToVoteType.parse(voteType),
      postalVoteOption,
      deliveryMethod,
      false
    )
  ) (
    postalVote => Some(
      postalVote.typeVote.name,
      postalVote.postalVoteOption,
      postalVote.deliveryMethod,
      Some(postalVote.forceRedirectToPostal)
    )
  ) verifying (validVoteOption)

  val postalOrProxyVoteForm = ErrorTransformForm(
    mapping(
      keys.postalOrProxyVote.key -> optional(postalOrProxyVoteMapping)
    ) (
        postalVote => InprogressOverseas (postalOrProxyVote = postalVote)
    ) (
        inprogress => Some(inprogress.postalOrProxyVote)
    ) verifying questionIsRequiredOverseas
  )
}

trait PostalOrProxyVoteOverseasConstraints extends PostalOrProxyVoteConstraints {
	  self: ErrorMessages
	    with FormKeys =>
	      
	  lazy val questionIsRequiredOverseas = Constraint[InprogressOverseas](keys.postalOrProxyVote.key) {
	    _.postalOrProxyVote match {
	      case None => Invalid("Please answer this question", keys.postalOrProxyVote.optIn)
	      case _ => Valid
	    }
	  }

	}
