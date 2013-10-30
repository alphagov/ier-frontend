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

    final def validateSession[A](bodyParser: BodyParser[A], block:Request[A] => InprogressApplication => Result):Action[A] = Action(bodyParser) {
      request =>
        request.session.getToken match {
          case Some(token) => isValidToken(token) match {
            case true => block(request)(request.session.getApplication).refreshSession()
            case false => Redirect(routes.RegisterToVoteController.index()).withFreshSession()
          }
          case None => Redirect(routes.RegisterToVoteController.index()).withFreshSession()
        }
    }

    final def withParser[A](bodyParser: BodyParser[A]) = new {
      def requiredFor(action: Request[A] => InprogressApplication => Result) = validateSession(bodyParser, action)
    }

    final def requiredFor(action: Request[AnyContent] => InprogressApplication => Result) = withParser(BodyParsers.parse.anyContent) requiredFor action
  }

  trait SessionKeys {
    val sessionPayloadKey = "application"
    val sessionTokenKey = "sessionKey"
  }

  implicit class SimpleResultToSession(result:Result) extends SessionKeys {
    def mergeWithSession(application: InprogressApplication)(implicit request: play.api.mvc.Request[_]) = {
      result.withSession(request.session.merge(application))
    }
    def withFreshSession() = {
      result.withSession(Session(Map(sessionTokenKey -> DateTime.now.toString())))
    }
    def refreshSession() = {
      result.withSession(sessionTokenKey -> DateTime.now.toString())
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
