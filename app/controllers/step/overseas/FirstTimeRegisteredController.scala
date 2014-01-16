package controllers.step.overseas

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressOverseas

object FirstTimeRegisteredController extends StubController {
  def firstTimeStep = route[InprogressOverseas]("/register-to-vote/overseas/first-time-registered")
}
