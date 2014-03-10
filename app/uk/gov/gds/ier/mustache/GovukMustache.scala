package uk.gov.gds.ier.mustache

import controllers.routes.Assets
import controllers.routes.RegisterToVoteController

trait GovukMustache {

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
          mainstream = Assets.at("stylesheets/mainstream.css").url,
          print = Assets.at("stylesheets/print.css").url,
          ie8 = Assets.at("stylesheets/application-ie8.css").url,
          ie7 = Assets.at("stylesheets/application-ie7.css").url,
          ie6 = Assets.at("stylesheets/application-ie6.css").url,
          application = Assets.at("stylesheets/application.css").url
        )
      )
    }

    def scripts() = {
      Mustache.render(
        "govuk/scripts",
        Scripts(
          jquery = Assets.at("javascripts/vendor/jquery/jquery-1.10.1.min.js").url,
          core = Assets.at("javascripts/core.js").url
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
      start,
      RegisterToVoteController.registerToVote.url,
      "#",
      "#"
    )

    def forcesStartPage() = {
      MainStepTemplate(
        content = Mustache.render(
          "govuk/registerToVoteForces",
          govukUrls(RegisterToVoteController.registerToVoteForcesStart.url)
        ),
        title = "Register to Vote (Armed Forces) - GOV.UK",
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
        title = "Register to Vote (Crown Servant or British Council) - GOV.UK",
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
        title = "Register to Vote - GOV.UK",
        insideHeader = Govuk.search(),
        related = Govuk.related(),
        scripts = Govuk.scripts(),
        header = Govuk.stylesheets()
      )
    }
  }
}
