package uk.gov.gds.ier.controller

import play.api.mvc._
import views._
import uk.gov.gds.ier.config.Config
import com.google.inject.Inject
import uk.gov.gds.ier.guice.WithConfig

class ErrorController @Inject ()(val config: Config)
  extends Controller
    with WithConfig {

  def timeout = Action {
    Ok(html.error.timeoutError(config.sessionTimeout))
  }
}
