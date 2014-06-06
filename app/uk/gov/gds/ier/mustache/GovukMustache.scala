package uk.gov.gds.ier.mustache

import controllers.routes.RegisterToVoteController
import uk.gov.gds.ier.guice.WithRemoteAssets
import play.api.templates.Html

trait GovukMustache extends StepMustache {
  self: WithRemoteAssets =>

  object Govuk {

    abstract class StartPage(
        templatePath: String,
        pageTitle: String
    ) extends Mustachio(templatePath) {
      override def render() = GovukTemplate(
        mainContent = super.render(),
        pageTitle = pageTitle,
        insideHeader = Search(),
        relatedContent = Related(),
        bodyEndContent = Some(Scripts()),
        head = Stylesheets(),
        propositionHeader = Html.empty,
        headerClass = ""
      )
    }

    case class Stylesheets(
        mainstream:String = remoteAssets.getAssetPath("stylesheets/mainstream.css").url,
        print:String = remoteAssets.getAssetPath("stylesheets/print.css").url,
        ie8:String = remoteAssets.getAssetPath("stylesheets/application-ie8.css").url,
        ie7:String = remoteAssets.getAssetPath("stylesheets/application-ie7.css").url,
        ie6:String = remoteAssets.getAssetPath("stylesheets/application-ie6.css").url,
        application:String = remoteAssets.getAssetPath("stylesheets/application.css").url
    ) extends Mustachio("govuk/stylesheets")

    case class Scripts(
        jquery:String = remoteAssets.getAssetPath("javascripts/vendor/jquery/jquery-1.10.1.min.js").url,
        core:String = remoteAssets.getAssetPath("javascripts/core.js").url
    ) extends Mustachio("govuk/scripts")

    case class Related() extends Mustachio("govuk/related")

    case class Search() extends Mustachio("govuk/search")
  }

  object RegisterToVote {
    trait GovukUrls {
      val startUrl:String
      val registerToVoteUrl:String = RegisterToVoteController.registerToVote.url
      val registerArmedForcesUrl:String = RegisterToVoteController.registerToVoteForces.url
      val registerCrownServantUrl:String = RegisterToVoteController.registerToVoteCrown.url
    }

    case class ForcesStartPage(
        startUrl:String = RegisterToVoteController.registerToVoteForcesStart.url
    ) extends Govuk.StartPage(
      "govuk/registerToVoteForces",
      "Register to Vote (Armed Forces)"
    ) with GovukUrls

    case class CrownStartPage(
        startUrl:String = RegisterToVoteController.registerToVoteCrownStart.url
    ) extends Govuk.StartPage(
      "govuk/registerToVoteCrown",
      "Register to Vote (Crown Servant or British Council)"
    ) with GovukUrls

    case class OrdinaryStartPage (
        startUrl: String = RegisterToVoteController.registerToVoteStart.url
    ) extends Govuk.StartPage(
      "govuk/registerToVoteOrdinary",
      "Register to Vote"
    ) with GovukUrls

    case class PrivacyPage() extends GovukPage(
      "govuk/privacy",
      "Register to vote: privacy",
      "article"
    )
  }
}
