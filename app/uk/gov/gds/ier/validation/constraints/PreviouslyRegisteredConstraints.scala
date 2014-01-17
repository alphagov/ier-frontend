package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.model.{PreviouslyRegistered, InprogressOverseas}
import play.api.data.validation.{Valid, Constraint, Invalid}

trait PreviouslyRegisteredConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val previouslyRegisteredFilled = Constraint[InprogressOverseas](keys.previouslyRegistered.key) { application =>
    application.previouslyRegistered match {
      case Some(PreviouslyRegistered(_)) => Valid
      case _ => Invalid("Please answer this question", keys.previouslyRegistered.hasPreviouslyRegistered)
    }
  }
}
