package uk.gov.gds.ier.transaction.forces.applicationFormVote

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.model.Contact
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.overseas.PostalOrProxyVoteConstraints
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.transaction.forces.InprogressForces

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
    keys.deliveryMethod.key -> optional(voteDeliveryMethodMapping)
  ) (
    (voteType, postalVoteOption, deliveryMethod) => PostalOrProxyVote(
      WaysToVoteType.parse(voteType),
      postalVoteOption,
      deliveryMethod
    )
  ) (
    postalVote => Some(
      postalVote.typeVote.name,
      postalVote.postalVoteOption,
      postalVote.deliveryMethod
    )
  ) verifying (validVoteOption)

  val postalOrProxyVoteForm = ErrorTransformForm(
    mapping(
      keys.postalOrProxyVote.key -> optional(postalOrProxyVoteMapping),
      keys.contact.key -> optional(Contact.mapping)
    ) (
      (postalVote, contact) => InprogressForces (postalOrProxyVote = postalVote, contact = contact)
    ) (
        inprogress => Some(inprogress.postalOrProxyVote, inprogress.contact)
    ) verifying questionIsRequiredForces
  )
}

trait PostalOrProxyVoteForcesConstraints extends PostalOrProxyVoteConstraints {
  self: ErrorMessages
    with FormKeys =>
      
  lazy val questionIsRequiredForces = Constraint[InprogressForces](keys.postalOrProxyVote.key) {
    _.postalOrProxyVote match {
      case None => Invalid("Please answer this question", keys.postalOrProxyVote.optIn)
      case _ => Valid
    }
  }

}
