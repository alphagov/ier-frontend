package uk.gov.gds.ier.step

import uk.gov.gds.ier.mustache.{MustacheModel, MustacheTemplateFactories}

trait StepTemplate[T] extends MustacheModel with MustacheTemplateFactories[T] {

  type Call = play.api.mvc.Call
  val Call = play.api.mvc.Call
  type Html = play.api.templates.Html
  type Lang = play.api.i18n.Lang
  type MustacheTemplate = uk.gov.gds.ier.mustache.MustacheTemplate[T]
  type MustacheRenderer = uk.gov.gds.ier.mustache.MustacheRenderer[T]
  type MustacheData = uk.gov.gds.ier.mustache.MustacheData

  val mustache: MustacheTemplate

}
