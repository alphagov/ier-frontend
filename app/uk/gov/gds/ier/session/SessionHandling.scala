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

abstract class SessionHandling[T <: InprogressApplication[T]] extends ResultHandling with SessionCleaner {
  self: WithSerialiser
    with Controller
    with Logging
    with WithConfig
    with WithEncryption =>

  def factoryOfT():T

  object ValidSession {

    final def validateSessionAndStore[A, B <: InprogressApplication[B]](bodyParser: BodyParser[A], block:Request[A] => T => (Result, B))(implicit manifest:Manifest[T]):Action[A] = Action(bodyParser) {
      request =>
        logger.debug(s"REQUEST ${request.method} ${request.path} - Valid Session needed")
        request.getToken match {
          case Some(token) => {
            isValidToken(token) match {
              case true => {
                logger.debug(s"Validate session and store - token is valid")
                val application = request.getApplication.getOrElse(factoryOfT())
                val (result, resultApplication) = block(request)(application)
                result.refreshSessionAndStore(resultApplication)
              }
              case false => {
                logger.debug(s"Validate session and store - token is not valid")
                Redirect(routes.RegisterToVoteController.index()).withFreshSession()
              }
            }
          }
          case None => {
            logger.debug(s"Validate session and store - Request has no token, refreshing and redirecting to govuk start page")
            Redirect(routes.RegisterToVoteController.index()).withFreshSession()
          }
        }
    }

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
      def storeAfter(action: Request[A] => T => (Result, T)) = validateSessionAndStore(bodyParser, action)
      def requiredFor(action: Request[A] => T => Result) = validateSession(bodyParser, action)
    }

    final def storeAfter(action: Request[AnyContent] => T => (Result, T))(implicit manifest:Manifest[T])  = withParser(BodyParsers.parse.anyContent) storeAfter action

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

  private implicit class InProgressRequest(request:play.api.mvc.Request[_]) extends SessionKeys {
    def getToken = {
      val cookie = request.cookies.get(sessionTokenKey)
      if (cookie.isDefined) {
        val sessionTokenKeyCookie = request.cookies.get(sessionTokenCookieKeyParam)
        if (sessionTokenKeyCookie.isDefined) {
          val decryptedInfo = encryptionService.decrypt(cookie.get.value, sessionTokenKeyCookie.get.value ,encryptionKeys.cookies.getPrivate)
          Some(decryptedInfo)
        }
        else None
      }
      else None

    }
    def getApplication(implicit manifest:Manifest[T]) : Option[T] = {
      request.cookies.get(sessionPayloadKey) flatMap { cookie =>
        val payloadKeyCookie = request.cookies.get(payloadCookieKeyParam)
        payloadKeyCookie map { key =>
          val decryptedInfo = encryptionService.decrypt(cookie.value, key.value, encryptionKeys.cookies.getPrivate)
          serialiser.fromJson[T](decryptedInfo)
        }
      }
    }
  }

}

trait SessionKeys {
  val sessionPayloadKey = "application"
  val sessionTokenKey = "sessionKey"

  val sessionTokenCookieKeyParam = "sessionTokenCookieKey"
  val payloadCookieKeyParam = "payloadCookieKey"
}

trait ResultHandling {
  self: WithEncryption with WithSerialiser with WithConfig =>

  implicit class InProgressResult(result:Result) extends SessionKeys {
    def emptySession()(implicit request: play.api.mvc.Request[_]) = {
      val requestCookies = DiscardingCookie(sessionPayloadKey) ::
        DiscardingCookie(sessionTokenKey) :: Nil

      result.discardingCookies(requestCookies: _*)
    }

    def withFreshSession() = {
      val (encryptedSessionTokenValue, sessionTokenCookieKey) = encryptionService.encrypt(DateTime.now.toString(), encryptionKeys.cookies.getPublic)
      result.withCookies(
        createSecureCookie(sessionTokenKey, encryptedSessionTokenValue.filter(_ >= ' ')),
        createSecureCookie(sessionTokenCookieKeyParam, sessionTokenCookieKey.filter(_ >= ' ')))
        .discardingCookies(DiscardingCookie(sessionPayloadKey))
    }

    def refreshSessionAndStore[B <: InprogressApplication[B]](application:B) = {
      val (encryptedSessionPayloadValue, payloadCookieKey) = encryptionService.encrypt(serialiser.toJson(application), encryptionKeys.cookies.getPublic)
      val (encryptedSessionTokenValue, sessionTokenCookieKey) = encryptionService.encrypt(DateTime.now.toString(), encryptionKeys.cookies.getPublic)
      result.withCookies(
        createSecureCookie(sessionTokenKey, encryptedSessionTokenValue.filter(_ >= ' ')),
        createSecureCookie(sessionPayloadKey, encryptedSessionPayloadValue.filter(_ >= ' ')),
        createSecureCookie(payloadCookieKeyParam, payloadCookieKey.filter(_ >= ' ')),
        createSecureCookie(sessionTokenCookieKeyParam, sessionTokenCookieKey.filter(_ >= ' ')))
    }

    def refreshSession() = {
      val (encryptedSessionTokenValue, sessionTokenCookieKey) = encryptionService.encrypt(DateTime.now.toString(), encryptionKeys.cookies.getPublic)
      result.withCookies(
        createSecureCookie(sessionTokenKey, encryptedSessionTokenValue.filter(_ >= ' ')),
        createSecureCookie(sessionTokenCookieKeyParam, sessionTokenCookieKey.filter(_ >= ' ')))
    }

    def createSecureCookie ( name : String, value : String) : Cookie = {
      Cookie (name, value, None, "/", None, config.cookiesSecured, true)
    }

  }

}

trait SessionCleaner extends ResultHandling {
  self: WithEncryption with WithSerialiser with WithConfig =>

  object NewSession {
    final def validateSession[A](bodyParser: BodyParser[A], block:Request[A] => Result):Action[A] = Action(bodyParser) {
      request =>
        block(request).withFreshSession()
    }

    final def withParser[A](bodyParser: BodyParser[A]) = new {
      def requiredFor(action: Request[A] => Result) = validateSession(bodyParser, action)
    }

    final def requiredFor(action: Request[AnyContent] => Result) = withParser(BodyParsers.parse.anyContent) requiredFor action

  }

  object ClearSession {
    final def eradicateSession[A](bodyParser: BodyParser[A], block:Request[A] => Result):Action[A] = Action(bodyParser) {
      implicit request =>
        block(request).emptySession()
    }

    final def withParser[A](bodyParser: BodyParser[A]) = new {
      def requiredFor(action: Request[A] => Result) = eradicateSession(bodyParser, action)
    }

    final def requiredFor(action: Request[AnyContent] => Result) = withParser(BodyParsers.parse.anyContent) requiredFor action
  }

}