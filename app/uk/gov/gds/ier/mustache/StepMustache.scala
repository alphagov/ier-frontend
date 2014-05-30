package uk.gov.gds.ier.mustache

import play.api.templates.Html
import uk.gov.gds.ier.guice.WithRemoteAssets
import play.api.http.{ContentTypeOf, MimeTypes}
import play.api.mvc.Content
import uk.gov.gds.ier.langs.Language
import play.api.mvc.Request

trait StepMustache extends MustacheModel {
  self: WithRemoteAssets =>

  def Mustache = org.jba.Mustache
  type MessagesForMustache = uk.gov.gds.ier.langs.MessagesForMustache
  type Lang = play.api.i18n.Lang

  implicit def requestToLang(request: Request[Any]): Lang = Language.getLang(request)

  implicit def mustachioContentType:ContentTypeOf[Mustachio] = ContentTypeOf(Some(MimeTypes.HTML))
  implicit def renderMustachioToHtml[T <: Mustachio](mustache:T):Html = mustache.render()

  abstract class Mustachio(mustachePath:String) extends Content {
    def render():Html = {
      Mustache.render(mustachePath, this)
    }

    def body: String = this.render().toString() match {
      case s:String => s
      case _ => ""
    }

    def contentType: String = MimeTypes.HTML
  }

  abstract class GovukPage(
      templatePath: String,
      pageTitle: String,
      contentClasses: String = ""
  ) extends Mustachio(templatePath) {
    override def render() = GovukTemplate(
      mainContent = super.render(),
      pageTitle = pageTitle,
      contentClasses = contentClasses
    )
  }

  case class GovukTemplate(
      htmlLang: String = "en",
      topOfPage: String = "",
      pageTitle: String = "",
      assetPath: String = remoteAssets.templatePath,
      head: Html = Head(),
      bodyClasses: String = "",
      headerClass: String = "with-proposition",
      insideHeader: Html = Html.empty,
      propositionHeader: Html = PropositionHeader(),
      afterHeader: String = "",
      cookieMessage: Html = CookieMessage(),
      footerTop: String = "",
      footerSupportLinks: Html = FooterLinks(),
      bodyEndContent: Option[Html] = None,
      mainContent: Html = Html.empty,
      relatedContent: Html = Html.empty,
      contentClasses: String = ""
  ) extends Mustachio("govuk_template_mustache/views/layouts/govuk_template") {
    val bodyEnd:Html = bodyEndContent getOrElse StepBodyEnd(
      messagesPath = remoteAssets.messages(htmlLang).url
    )

    val content: Html = ContentTemplate(
      mainContent,
      relatedContent,
      contentClasses
    ).render()
  }

  case class ContentTemplate(
      content: Html,
      related: Html,
      contentClasses: String
  ) extends Mustachio("template/content")

  case class CookieMessage() extends Mustachio("template/cookieMessage")

  case class FooterLinks(
    cookieUrl: String = controllers.routes.RegisterToVoteController.cookies.url,
    privacyUrl: String = controllers.routes.RegisterToVoteController.privacy.url
  ) extends Mustachio("template/footerLinks")

  case class PropositionHeader() extends Mustachio("template/propositionHeader")

  case class StepBodyEnd(
      assetPath: String = remoteAssets.assetsPath,
      messagesPath: String = remoteAssets.messages("en").url
  ) extends Mustachio("template/stepBodyEnd")

  case class Head (
      assetPath: String = remoteAssets.assetsPath
  ) extends Mustachio("template/head")
}
