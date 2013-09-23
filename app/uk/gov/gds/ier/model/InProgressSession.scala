package uk.gov.gds.ier.model

import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.mvc.Session

trait InProgressSession extends IerForms {
  self: WithSerialiser =>

  implicit class InprogressApplicationToSession(app:InprogressApplication) {
    private val sessionKey = "application"
    def toSession:(String, String) = {
      sessionKey -> toJson(app)
    }
  }

  implicit class InProgressSession(session:Session) {
    private val sessionKey = "application"
    def getApplication = {
      session.get(sessionKey) match {
        case Some(app) => fromJson[InprogressApplication](app)
        case _ => InprogressApplication()
      }
    }
    def merge(application: InprogressApplication):InprogressApplication= {
      val stored = getApplication
      stored.copy(
        name = application.name.orElse(stored.name),
        previousName = application.previousName.orElse(stored.previousName),
        dobYear = application.dobYear.orElse(stored.dobYear),
        dobMonth = application.dobMonth.orElse(stored.dobMonth),
        dobDay = application.dobDay.orElse(stored.dobDay),
        nationality = application.nationality.orElse(stored.nationality),
        nino = application.nino.orElse(stored.nino),
        address = application.address.orElse(stored.address),
        postcode = application.postcode.orElse(stored.postcode),
        movedRecently = application.movedRecently.orElse(stored.movedRecently),
        previousAddress = application.previousAddress.orElse(stored.previousAddress),
        previousPostcode = application.previousPostcode.orElse(stored.previousPostcode),
        hasOtherAddress = application.hasOtherAddress.orElse(stored.hasOtherAddress),
        otherAddress = application.otherAddress.orElse(stored.otherAddress),
        otherPostcode = application.otherPostcode.orElse(stored.otherPostcode),
        openRegisterOptin = application.openRegisterOptin.orElse(stored.openRegisterOptin),
        contact = application.contact.orElse(stored.contact)
      )
    }
  }
}