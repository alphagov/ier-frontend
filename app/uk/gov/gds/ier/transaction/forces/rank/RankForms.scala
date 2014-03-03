package uk.gov.gds.ier.transaction.forces.rank

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import play.api.data.Forms._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.model.InprogressForces
import scala.Some

trait RankForms {
  self:  FormKeys
    with ErrorMessages =>

  lazy val rankMapping = mapping(
    keys.serviceNumber.key -> optional(nonEmptyText),
    keys.rank.key -> optional(nonEmptyText)
  ) (
    (serviceNumber, rank) => Rank(serviceNumber, rank)
  ) (
    rank => Some(rank.serviceNumber, rank.rank)
  )

  val rankForm = ErrorTransformForm(
    mapping(
      keys.rank.key -> optional(rankMapping)
    ) (
      rank => InprogressForces(rank = rank)
    ) (
      inprogressApplication => Some(inprogressApplication.rank)
    )
  )
}

