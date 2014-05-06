package uk.gov.gds.ier.session

import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.mvc._
import controllers.routes
import org.joda.time.DateTime
import uk.gov.gds.ier.logging.Logging
import scala.Some
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.step.InprogressApplication

abstract class SessionHandling[T <: InprogressApplication[T]]
  extends RequestHandling
  with SessionTokenValidator
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
            token.isValid match {
              case true => {
                val application = request.getApplication.getOrElse(factoryOfT())
                logger.debug(s"Validate session - token is valid")
                val result = block(request)(application)
                result storeToken token.refreshToken
              }
              case false => {
                logger.debug(s"Validate session - token is not valid")
                Redirect(routes.ErrorController.timeout()).withFreshSession()
              }
            }
          }
          case None => {
            logger.debug(s"Validate session - Request has no token, refreshing and redirecting to govuk start page")
            Redirect(routes.RegisterToVoteController.registerToVote()).withFreshSession()
          }
        }
    }

    final def withParser[A](bodyParser: BodyParser[A])(implicit manifest:Manifest[T]) = new {
      def requiredFor(action: Request[A] => T => Result) = validateSession(bodyParser, action)
    }

    final def requiredFor(action: Request[AnyContent] => T => Result)(implicit manifest:Manifest[T])  = withParser(BodyParsers.parse.anyContent) requiredFor action


  }
}
