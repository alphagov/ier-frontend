package uk.gov.gds.ier.mustache

import play.api.templates.Html
import views.html.layouts.{stepsBodyEnd, head}
import uk.gov.gds.ier.validation.{FormKeys, Key, ErrorTransformForm}
import uk.gov.gds.ier.guice.WithRemoteAssets
import play.api.i18n.Lang

trait StepMustache extends MustacheModel {
  self: WithRemoteAssets =>

  def Mustache = org.jba.Mustache

  case class TemplateModel(
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
  )

  case class ContentModel(
      content: Html,
      related: Html,
      contentClasses: String
  )

  def contentTemplate(model: ContentModel) = {
    Mustache.render("template/content", model)
  }

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
    Mustache.render (
      "govuk_template_mustache/views/layouts/govuk_template",
      TemplateModel (
        pageTitle = s"$title - GOV.UK",
        content = Mustache.render(
          "template/content",
          ContentModel(content, related, contentClasses.getOrElse(""))
        ),
        bodyEnd = scripts,
        head = header,
        insideHeader = insideHeader,
        footerSupportLinks = Mustache.render("template/footerLinks", null),
        cookieMessage = Mustache.render("template/cookieMessage", null),
        htmlLang = lang.language
      )
    )
  }
}
