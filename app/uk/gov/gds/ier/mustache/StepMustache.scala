package uk.gov.gds.ier.mustache

import play.api.templates.Html
import views.html.layouts.{stepsBodyEnd, head}
import uk.gov.gds.ier.validation.{FormKeys, Key, ErrorTransformForm}
import uk.gov.gds.ier.guice.WithRemoteAssets

trait StepMustache extends MustacheModel {
  self: WithRemoteAssets =>

  def Mustache = org.jba.Mustache

  case class TemplateModel(
      topOfPage: String,
      htmlLang: String,
      pageTitle: String,
      assetPath: String,
      head: String,
      bodyClasses: String,
      headerClass: String,
      insideHeader: String,
      propositionHeader: String,
      afterHeader: String,
      cookieMessage: String,
      content: String,
      footerTop: String,
      footerSupportLinks: String,
      bodyEnd: String
  )

  def MainStepTemplate(
      content:Html,
      title: String,
      header:Html = head(),
      scripts:Html = stepsBodyEnd(),
      related:Html = Html.empty,
      insideHeader:Html = Html.empty,
      contentClasses:Option[String] = None
  ) = {
    views.html.layouts.main (
      title = Some(s"$title - GOV.UK"),
      stylesheets = header,
      scripts = scripts,
      insideHeader = insideHeader,
      related = related,
      contentClasses = contentClasses
    )(content)
  }
}
