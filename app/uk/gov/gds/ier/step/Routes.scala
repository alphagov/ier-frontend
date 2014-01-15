package uk.gov.gds.ier.step

import play.api.mvc.Call

case class Routes(get:Call, post:Call)

trait WithRoutes {
  val routes:Routes
}
