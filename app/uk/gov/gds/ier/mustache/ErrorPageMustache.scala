package uk.gov.gds.ier.mustache

import controllers.routes.RegisterToVoteController

trait ErrorPageMustache {
  object ErrorPage extends StepMustache {

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
