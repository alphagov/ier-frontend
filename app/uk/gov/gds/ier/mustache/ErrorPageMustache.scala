package uk.gov.gds.ier.mustache

import controllers.routes.RegisterToVoteController
import uk.gov.gds.ier.guice.WithRemoteAssets

trait ErrorPageMustache extends StepMustache {
  self: WithRemoteAssets =>

  object ErrorPage {

    def ServerError() = {
      val html = Mustache.render("error/serverError", null)

      MainStepTemplate(
        content = html,
        title = "Oops, we've done something wrong"
      )
    }

    case class NotFoundModel(url:String, startPageUrl:String)

    def NotFound(url:String) = {
      val html = Mustache.render(
        "error/notFound",
        NotFoundModel(url, RegisterToVoteController.redirectToOrdinary.url)
      )

      MainStepTemplate(
        content = html,
        title = "This isn't the page you were looking for"
      )
    }
  }
}
