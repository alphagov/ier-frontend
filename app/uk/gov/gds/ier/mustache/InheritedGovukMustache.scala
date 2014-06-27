package uk.gov.gds.ier.mustache

import uk.gov.gds.ier.guice.{WithConfig, WithRemoteAssets}
import uk.gov.gds.ier.langs.Language

trait InheritedGovukMustache extends StepMustache {
  self: WithRemoteAssets
    with WithConfig =>

  abstract class GovukInheritedTemplate(
      template: String
  ) extends Mustachio (template) with MessagesForMustache {

    val pageTitle = ""
    val contentClasses = ""
    val sourcePath = ""

    val headerClass = "with-proposition"
    val messagesPath = remoteAssets.messages("en").url
    val assetPath = remoteAssets.templatePath
    val appAssetPath = remoteAssets.assetsPath
    val startUrl = config.ordinaryStartUrl
    val cookieUrl = controllers.routes.RegisterToVoteController.cookies.url
    val privacyUrl = controllers.routes.RegisterToVoteController.privacy.url
  }
}
