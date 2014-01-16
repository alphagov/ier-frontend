package controllers.step.overseas

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressOverseas

object DateLeftUkController extends StubController {
  def dateLeftUkStep = route[InprogressOverseas]("/register-to-vote/overseas/date-left-uk")
}
