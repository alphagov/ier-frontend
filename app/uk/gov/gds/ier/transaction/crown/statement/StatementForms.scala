package uk.gov.gds.ier.transaction.crown.statement

import uk.gov.gds.ier.validation._
import play.api.data.Forms._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.model.InprogressCrown
import scala.Some
import play.api.data.validation.{Invalid, Valid, Constraint}


trait StatementForms extends StatementConstraints {
  self:  FormKeys
    with ErrorMessages =>

  lazy val statementMapping = mapping(
    keys.crownMember.key -> optional(boolean),
    keys.partnerCrownMember.key -> optional(boolean),
    keys.britishCouncilMember.key -> optional(boolean),
    keys.partnerBritishCouncilMember.key -> optional(boolean)

  ) (
    CrownStatement.apply
  ) (
    CrownStatement.unapply
  )

  val statementForm = ErrorTransformForm(
    mapping(
      keys.statement.key -> optional(statementMapping)
    ) (
      statement => InprogressCrown (statement = statement)
    ) (
      inprogress => Some(inprogress.statement)
    ).verifying (atLeastOneStatementSelected)
  )
}


trait StatementConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val atLeastOneStatementSelected = Constraint[InprogressCrown](keys.statement.key) {
    application =>
      application.statement match {
        case None => Invalid("Please answer this question", keys.statement)
        case _ => Valid
    }
  }
}

