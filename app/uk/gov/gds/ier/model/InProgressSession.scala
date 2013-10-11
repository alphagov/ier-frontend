package uk.gov.gds.ier.model

import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.mvc._
import uk.gov.gds.ier.validation.IerForms
import controllers.routes
import scala.Some
import org.joda.time.DateTime
import scala.concurrent.Future


trait InProgressSession extends IerForms {
  self: WithSerialiser with Controller =>

  object ValidSession {

    private def isValidToken(token:String) = {
      try {
        val dt = DateTime.parse(token)
        dt.isAfter(DateTime.now.minusMinutes(30))
      } catch {
        case e:IllegalArgumentException => false
      }
    }

    def requiredFor[A](action: Action[A]) = apply(action)

    def apply[A](action: Action[A]): Action[A] = apply(action.parser)(action)

    def apply[A](bodyParser: BodyParser[A])(action: Action[A]): Action[A] = new Action[A] {
      def parser = bodyParser

      def apply(ctx: Request[A]) = {
        ctx.session.getToken match {
          case Some(token) => isValidToken(token) match {
            case true => action(ctx)
            case false => Future.successful(Redirect(routes.RegisterToVoteController.index()))
          }
          case None => Future.successful(Redirect(routes.RegisterToVoteController.index()))
        }
      }
    }
  }

  trait SessionKeys {
    val sessionPayloadKey = "application"
    val sessionTokenKey = "sessionKey"
  }

  implicit class SimpleResultToSession(result:SimpleResult) extends SessionKeys {
    def mergeWithSession(application: InprogressApplication)(implicit request: play.api.mvc.Request[_]) = {
      result.withSession(request.session.merge(application))
    }
    def withFreshSession() = {
      result.withSession(Session(Map(sessionTokenKey -> DateTime.now.toString())))
    }
  }

  implicit class InprogressApplicationToSession(app:InprogressApplication) extends SessionKeys {
    def toSession:(String, String) = {
      sessionPayloadKey -> toJson(app)
    }
  }

  implicit class InProgressSession(session:Session) extends SessionKeys {
    def getToken = {
      session.get(sessionTokenKey)
    }
    def getApplication = {
      session.get(sessionPayloadKey) match {
        case Some(app) => fromJson[InprogressApplication](app)
        case _ => InprogressApplication()
      }
    }
    def merge(application: InprogressApplication):Session = {
      val stored = getApplication
      session + stored.copy(
        name = application.name.orElse(stored.name),
        previousName = application.previousName.orElse(stored.previousName),
        dob = application.dob.orElse(stored.dob),
        nationality = application.nationality.orElse(stored.nationality),
        nino = application.nino.orElse(stored.nino),
        address = application.address.orElse(stored.address),
        previousAddress = application.previousAddress.orElse(stored.previousAddress),
        otherAddress = application.otherAddress.orElse(stored.otherAddress),
        openRegisterOptin = application.openRegisterOptin.orElse(stored.openRegisterOptin),
        postalVoteOptin = application.postalVoteOptin.orElse(stored.postalVoteOptin),
        contact = application.contact.orElse(stored.contact)
      ).toSession
    }
  }
}
