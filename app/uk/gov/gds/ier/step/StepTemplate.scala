package uk.gov.gds.ier.step

import uk.gov.gds.ier.mustache.{MustacheModel, MustacheTemplateFactories}
import uk.gov.gds.ier.validation.ErrorTransformForm

trait StepTemplate[T] extends MustacheModel with MustacheTemplateFactories[T] {

  type Call = play.api.mvc.Call
  val Call = play.api.mvc.Call
  type Html = play.api.templates.Html
  type MustacheTemplate[T] = uk.gov.gds.ier.mustache.MustacheTemplate[T]
  type MustacheRenderer[T] = uk.gov.gds.ier.mustache.MustacheRenderer[T]
  type MustacheData = uk.gov.gds.ier.mustache.MustacheData
  val MustacheData = uk.gov.gds.ier.mustache.MustacheData

  val mustache: MustacheTemplate[T]

}
