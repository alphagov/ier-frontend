package uk.gov.gds.ier.model

import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.mvc.Session
import uk.gov.gds.ier.validation.IerForms

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
        dob = application.dob.orElse(stored.dob),
        nationality = application.nationality.orElse(stored.nationality),
        nino = application.nino.orElse(stored.nino),
        address = application.address.orElse(stored.address),
        movedRecently = application.movedRecently.orElse(stored.movedRecently),
        previousAddress = application.previousAddress.orElse(stored.previousAddress),
        hasOtherAddress = application.hasOtherAddress.orElse(stored.hasOtherAddress),
        openRegisterOptin = application.openRegisterOptin.orElse(stored.openRegisterOptin),
        contact = application.contact.orElse(stored.contact)
      )
    }
  }
}