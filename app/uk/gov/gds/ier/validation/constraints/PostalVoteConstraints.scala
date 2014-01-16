package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages, NinoValidator}
import play.api.data.validation.{Valid, Invalid, Constraint}
import uk.gov.gds.ier.model._

trait PostalVoteConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val validDeliveryMethod = Constraint[PostalVoteDeliveryMethod](keys.deliveryMethod.key) {
    postaVoteDeliveryMethod =>
      if (postaVoteDeliveryMethod.deliveryMethod.isDefined)
        if (postaVoteDeliveryMethod.deliveryMethod == Some("email") && !postaVoteDeliveryMethod.emailAddress.isDefined )
          Invalid("Please enter the email address", keys.deliveryMethod.emailAddress)
        else Valid
      else Invalid("Please answer this question", keys.deliveryMethod.methodName)
  }

  lazy val validPostVoteOption = Constraint[InprogressOrdinary](keys.deliveryMethod.key) {
    application => {
      if (application.postalVoteOptin.isDefined && application.postalVoteOptin.get == true && !application.postalVoteDeliveryMethod.isDefined)
        Invalid("Please answer this question", keys.deliveryMethod.methodName)
      else
        Valid
    }
  }
}
