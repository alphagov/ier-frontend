package uk.gov.gds.ier.controller

import play.api.mvc.Controller
import uk.gov.gds.ier.session.SessionHandling
import uk.gov.gds.ier.serialiser.{JsonSerialiser, WithSerialiser}
import com.google.inject.Inject

class ExitController @Inject() (val serialiser: JsonSerialiser)
  extends Controller
  with SessionHandling
  with WithSerialiser {

  def scotland = ClearSession requiredFor {
    request =>
      Ok(views.html.exit.scotland())
  }

  def northernIreland = ClearSession requiredFor {
    request =>
      Ok(views.html.exit.northernIreland())
  }
}
