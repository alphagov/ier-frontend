package uk.gov.gds.ier.controller

import play.api.mvc.Controller
import uk.gov.gds.ier.session.SessionHandling
import uk.gov.gds.ier.serialiser.{JsonSerialiser, WithSerialiser}
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.guice.WithConfig

class ExitController @Inject() (val serialiser: JsonSerialiser,
                                val config: Config)
  extends Controller
  with SessionHandling
  with WithSerialiser
  with WithConfig {

  def scotland = ClearSession requiredFor {
    request =>
      Ok(views.html.exit.scotland())
  }

  def northernIreland = ClearSession requiredFor {
    request =>
      Ok(views.html.exit.northernIreland())
  }

  def under18 = ClearSession requiredFor {
    request =>
      Ok(views.html.exit.under18())
  }

  def tooYoung = ClearSession requiredFor {
    request =>
      Ok(views.html.exit.tooYoung())
  }

  def dontKnow = ClearSession requiredFor {
    request =>
      Ok(views.html.exit.dontKnow())
  }
}
