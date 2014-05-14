package uk.gov.gds.ier.step

import uk.gov.gds.ier.mustache.{
  MustacheModel,
  MustacheTemplateFactories,
  MustacheRendering}
import uk.gov.gds.ier.guice.WithRemoteAssets

trait StepTemplate[T]
    extends MustacheModel
    with MustacheTemplateFactories[T]
    with MustacheRendering[T]
    with WithRemoteAssets {

  type Call = play.api.mvc.Call
  val Call = play.api.mvc.Call
  type Html = play.api.templates.Html
  type Lang = play.api.i18n.Lang
  type MustacheTemplate = uk.gov.gds.ier.mustache.MustacheTemplate[T]
  type MustacheData = uk.gov.gds.ier.mustache.MustacheData
  val Messages = uk.gov.gds.ier.langs.Messages

  val mustache: MustacheTemplate

}
