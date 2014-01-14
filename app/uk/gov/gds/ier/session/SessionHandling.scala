package uk.gov.gds.ier.session

import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.mvc._
import controllers.routes
import org.joda.time.DateTime
import uk.gov.gds.ier.model.{InprogressOrdinary, InprogressApplication}
import uk.gov.gds.ier.logging.Logging
import play.api.mvc.DiscardingCookie
import play.api.mvc.Cookie
import scala.Some
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}

abstract class SessionHandling[T <: InprogressApplication[T]]
  extends ResultStoring
    with ResultCleaning
    with RequestHandling
    with SessionCleaner {
  self: WithSerialiser
    with Controller
    with Logging
    with WithConfig
    with WithEncryption =>

  def factoryOfT():T

  object ValidSession {

    final def validateSession[A](bodyParser: BodyParser[A], block:Request[A] => T => Result)(implicit manifest:Manifest[T]):Action[A] = Action(bodyParser) {
      request =>
        logger.debug(s"REQUEST ${request.method} ${request.path} - Valid Session needed")
        request.getToken match {
          case Some(token) => {
            isValidToken(token) match {
              case true => {
                val application = request.getApplication.getOrElse(factoryOfT())
                logger.debug(s"Validate session - token is valid")
                val result = block(request)(application)
                result.refreshSession()
              }
              case false => {
                logger.debug(s"Validate session - token is not valid")
                Redirect(routes.RegisterToVoteController.index()).withFreshSession()
              }
            }
          }
          case None => {
            logger.debug(s"Validate session - Request has no token, refreshing and redirecting to govuk start page")
            Redirect(routes.RegisterToVoteController.index()).withFreshSession()
          }
        }
    }

    final def withParser[A](bodyParser: BodyParser[A])(implicit manifest:Manifest[T]) = new {
      def requiredFor(action: Request[A] => T => Result) = validateSession(bodyParser, action)
    }

    final def requiredFor(action: Request[AnyContent] => T => Result)(implicit manifest:Manifest[T])  = withParser(BodyParsers.parse.anyContent) requiredFor action

    protected def isValidToken(token:String) = {
      try {
        val dt = DateTime.parse(token)
        dt.isAfter(DateTime.now.minusMinutes(config.sessionTimeout))
      } catch {
        case e:IllegalArgumentException => false
      }
    }
  }
}