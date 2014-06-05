package uk.gov.gds.ier.mustache

import controllers.routes.RegisterToVoteController
import uk.gov.gds.ier.guice.WithRemoteAssets

trait ErrorPageMustache extends StepMustache {
  self: WithRemoteAssets =>

  object ErrorPage {
    abstract class ErrorMustache(
        path: String,
        title: String
    ) extends GovukPage(path, title, "article")

    case class ServerError() extends ErrorMustache (
      "error/serverError",
      "Oops, we've done something wrong"
    )

    case class NotFound(
        url:String,
        startPageUrl:String = RegisterToVoteController.redirectToOrdinary.url
    ) extends ErrorMustache (
      "error/notFound",
      "This isn't the page you were looking for"
    )

    case class Timeout(
        timeout: Int,
        startUrl: String = RegisterToVoteController.registerToVote.url
    ) extends ErrorMustache (
      "error/timeout",
      "Register to Vote - Sorry, your session has expired"
    )
  }
}
