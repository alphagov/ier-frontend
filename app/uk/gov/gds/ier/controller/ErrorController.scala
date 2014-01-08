package uk.gov.gds.ier.controller

import play.api.mvc._
import views._

class ErrorController
  extends Controller {

  def timeout = Action {
    Ok(html.error.timeoutError())
  }
}

