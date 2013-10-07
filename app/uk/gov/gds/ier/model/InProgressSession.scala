package uk.gov.gds.ier.model

import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.mvc._
import uk.gov.gds.ier.validation.IerForms
import controllers.routes
import scala.Some


trait InProgressSession extends IerForms {
  self: WithSerialiser with Controller =>

  object ValidSession {

    def requiredFor[A](action: Action[A]) = apply(action)

    def apply[A](action: Action[A]): Action[A] = apply(action.parser)(action)

    def apply[A](bodyParser: BodyParser[A])(action: Action[A]): Action[A] = new Action[A] {
      def parser = bodyParser

      def apply(ctx: Request[A]) = {
        ctx.session.getToken match {
          case Some(token) => action(ctx)
          case None => Redirect(routes.RegisterToVoteController.index())
        }
      }
    }
  }

  implicit class InprogressApplicationToSession(app:InprogressApplication) {
    private val sessionKey = "application"
    def toSession:(String, String) = {
      sessionKey -> toJson(app)
    }
  }

  implicit class InProgressSession(session:Session) {
    private val sessionPayloadKey = "application"
    private val sessionTokenKey = "sessionKey"
    def getToken = {
      session.get(sessionTokenKey)
    }
    def getApplication = {
      session.get(sessionPayloadKey) match {
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
        previousAddress = application.previousAddress.orElse(stored.previousAddress),
        otherAddress = application.otherAddress.orElse(stored.otherAddress),
        openRegisterOptin = application.openRegisterOptin.orElse(stored.openRegisterOptin),
        contact = application.contact.orElse(stored.contact)
      )
    }
  }
}