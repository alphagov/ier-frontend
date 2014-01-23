package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation._
import play.api.data.validation.{Valid, Invalid, Constraint}
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.model.PostalVote
import scala.Some
import uk.gov.gds.ier.model.PostalVoteDeliveryMethod

trait PostalVoteConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val validDeliveryMethod = Constraint[PostalVoteDeliveryMethod](keys.deliveryMethod.key) {
    postaVoteDeliveryMethod =>
      if (postaVoteDeliveryMethod.deliveryMethod.isDefined)
        if (postaVoteDeliveryMethod.deliveryMethod == Some("email"))
          postaVoteDeliveryMethod.emailAddress.map(emailAddress =>
            if (EmailValidator.isValid(emailAddress)) Valid
            else Invalid("Please enter a valid email address", keys.postalVote.deliveryMethod.emailAddress)
          ).getOrElse(
            Invalid("Please enter your email address", keys.postalVote.deliveryMethod.emailAddress)
          )
        else Valid
      else Invalid("Please answer this question", keys.postalVote.deliveryMethod.methodName)
  }

  lazy val validPostVoteOption = Constraint[PostalVote](keys.postalVote.deliveryMethod.key) {
    postalVote => {
      if (postalVote.postalVoteOption == Some(true) &&
         !postalVote.deliveryMethod.isDefined)
        Invalid("Please answer this question", keys.postalVote.deliveryMethod.methodName)
      else
        Valid
    }
  }
}
