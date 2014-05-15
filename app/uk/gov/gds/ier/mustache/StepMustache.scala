package uk.gov.gds.ier.mustache

import play.api.templates.Html
import views.html.layouts.{stepsBodyEnd, head}
import uk.gov.gds.ier.validation.{FormKeys, Key, ErrorTransformForm}
import uk.gov.gds.ier.guice.WithRemoteAssets
import play.api.i18n.Lang

trait StepMustache extends MustacheModel {
  self: WithRemoteAssets =>

  def Mustache = org.jba.Mustache

  abstract class Mustachio(mustachePath:String) {
    def render():Html = {
      Mustache.render(mustachePath, this)
    }
  }

  case class GovukTemplate(
      topOfPage: String = "",
      htmlLang: String = "",
      pageTitle: String = "",
      assetPath: String = remoteAssets.templatePath,
      head: Html = Html.empty,
      bodyClasses: String = "",
      headerClass: String = "",
      insideHeader: Html = Html.empty,
      propositionHeader: String = "",
      afterHeader: String = "",
      cookieMessage: Html = Html.empty,
      content: Html = Html.empty,
      footerTop: String = "",
      footerSupportLinks: Html = Html.empty,
      bodyEnd: Html = Html.empty
  ) extends Mustachio("govuk_template_mustache/views/layouts/govuk_template")

  case class ContentTemplate(
      content: Html,
      related: Html,
      contentClasses: String
  ) extends Mustachio("template/content")

  case class CookieMessage() extends Mustachio("template/cookieMessage")

  case class FooterLinks() extends Mustachio("template/footerLinks")

  def MainStepTemplate(
      content:Html,
      title: String,
      header:Html = head(),
      scripts:Html = stepsBodyEnd(),
      related:Html = Html.empty,
      insideHeader:Html = Html.empty,
      contentClasses:Option[String] = None,
      lang: Lang = Lang("en")
  ) = {
    GovukTemplate (
      pageTitle = s"$title - GOV.UK",
      content = ContentTemplate(content, related, contentClasses.getOrElse("")).render(),
      bodyEnd = scripts,
      head = header,
      insideHeader = insideHeader,
      footerSupportLinks = FooterLinks().render(),
      cookieMessage = CookieMessage().render(),
      htmlLang = lang.language
    ).render()
  }
}
