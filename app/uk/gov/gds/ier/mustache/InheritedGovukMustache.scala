package uk.gov.gds.ier.mustache

import uk.gov.gds.ier.guice.{WithConfig, WithRemoteAssets}
import uk.gov.gds.ier.langs.Language

trait InheritedGovukMustache extends StepMustache {
  self: WithRemoteAssets
    with WithConfig =>

  case class TestPage (
      override val pageTitle:String = "This is an inherited template test - Register to Vote",
      override val contentClasses:String = "article"
  ) (
      implicit override val lang:Lang = Language.english
  ) extends InheritedMustachio("test/testpage")

  abstract class ArticleMustachio(
      template: String
  ) extends InheritedMustachio(template) {
    override val contentClasses = "article"
  }

  abstract class InheritedMustachio(
      template: String
  ) extends Mustachio(template) with GovukInheritedTemplate

  trait GovukInheritedTemplate extends MessagesForMustache {
    val lang = Language.english
    val htmlLang = lang.code
    val pageTitle = ""
    val contentClasses = ""
    val sourcePath = ""

    val headerClass = "with-proposition"
    val messagesPath = remoteAssets.messages(htmlLang).url
    val assetPath = remoteAssets.templatePath
    val appAssetPath = remoteAssets.assetsPath
    val startUrl = config.ordinaryStartUrl
    val cookieUrl = controllers.routes.RegisterToVoteController.cookies.url
    val privacyUrl = controllers.routes.RegisterToVoteController.privacy.url
  }
}
