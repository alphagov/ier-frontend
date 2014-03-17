package uk.gov.gds.ier.transaction.crown.statement

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressCrown
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache

trait StatementMustache extends StepMustache {

  case class StatementModel(
      question: Question,
      crown:Field,
      crownServant:Field,
      crownPartner:Field,
      council:Field,
      councilEmployee:Field,
      councilPartner:Field
  )
}
