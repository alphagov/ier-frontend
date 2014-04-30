package uk.gov.gds.ier.mustache

import controllers.routes.Assets
import controllers.routes.RegisterToVoteController
import controllers.RemoteAssets
import uk.gov.gds.ier.guice.WithRemoteAssets

trait GovukMustache {
  self: WithRemoteAssets =>

  object Govuk extends StepMustache {

    case class Stylesheets(mainstream:String,
                           print:String,
                           ie8:String,
                           ie7:String,
                           ie6:String,
                           application:String)

    case class Scripts(jquery:String,
                       core:String)

    def stylesheets() = {
      Mustache.render(
        "govuk/stylesheets",
        Stylesheets(
          mainstream = remoteAssets.at("stylesheets/mainstream.css").url,
          print = remoteAssets.at("stylesheets/print.css").url,
          ie8 = remoteAssets.at("stylesheets/application-ie8.css").url,
          ie7 = remoteAssets.at("stylesheets/application-ie7.css").url,
          ie6 = remoteAssets.at("stylesheets/application-ie6.css").url,
          application = remoteAssets.at("stylesheets/application.css").url
        )
      )
    }

    def scripts() = {
      Mustache.render(
        "govuk/scripts",
        Scripts(
          jquery = remoteAssets.at("javascripts/vendor/jquery/jquery-1.10.1.min.js").url,
          core = remoteAssets.at("javascripts/core.js").url
        )
      )
    }

    def related() = {
      Mustache.render("govuk/related", None)
    }

    def search() = {
      Mustache.render("govuk/search", None)
    }
  }

  object RegisterToVote extends StepMustache {
    case class GovukUrls(startUrl:String,
                         registerToVoteUrl:String,
                         registerArmedForcesUrl:String,
                         registerCrownServantUrl:String)

    def govukUrls(start:String) = GovukUrls(
      startUrl = start,
      registerToVoteUrl = RegisterToVoteController.registerToVote.url,
      registerArmedForcesUrl = RegisterToVoteController.registerToVoteForces.url,
      registerCrownServantUrl = RegisterToVoteController.registerToVoteCrown.url
    )

    def forcesStartPage() = {
      MainStepTemplate(
        content = Mustache.render(
          "govuk/registerToVoteForces",
          govukUrls(RegisterToVoteController.registerToVoteForcesStart.url)
        ),
        title = "Register to Vote (Armed Forces)",
        insideHeader = Govuk.search(),
        related = Govuk.related(),
        scripts = Govuk.scripts(),
        header = Govuk.stylesheets()
      )
    }

    def crownStartPage() = {
      MainStepTemplate(
        content = Mustache.render(
          "govuk/registerToVoteCrown",
          govukUrls(RegisterToVoteController.registerToVoteCrownStart.url)
        ),
        title = "Register to Vote (Crown Servant or British Council)",
        insideHeader = Govuk.search(),
        related = Govuk.related(),
        scripts = Govuk.scripts(),
        header = Govuk.stylesheets()
      )
    }

    def ordinaryStartPage() = {
      MainStepTemplate(
        content = Mustache.render(
          "govuk/registerToVoteOrdinary",
          govukUrls(RegisterToVoteController.registerToVoteStart.url)
        ),
        title = "Register to Vote",
        insideHeader = Govuk.search(),
        related = Govuk.related(),
        scripts = Govuk.scripts(),
        header = Govuk.stylesheets()
      )
    }
  }
}
