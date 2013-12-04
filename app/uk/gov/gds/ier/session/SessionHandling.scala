package uk.gov.gds.ier.session

import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.mvc._
import controllers.routes
import org.joda.time.DateTime
import uk.gov.gds.ier.model.InprogressApplication
import play.api.mvc.DiscardingCookie
import play.api.mvc.Cookie
import scala.Some
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}

trait SessionHandling {
  self: WithSerialiser
    with Controller
    with WithConfig
    with WithEncryption =>

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

  object ValidSession {

    final def validateSessionAndStore[A](bodyParser: BodyParser[A], block:Request[A] => InprogressApplication => (Result, InprogressApplication)):Action[A] = Action(bodyParser) {
      request =>
        request.getToken match {
          case Some(token) => isValidToken(token) match {
            case true => {
              val (result, application) = block(request)(request.getApplication)
              result.refreshSessionAndStore(application)
            }
            case false => Redirect(routes.RegisterToVoteController.index()).withFreshSession()
          }
          case None => Redirect(routes.RegisterToVoteController.index()).withFreshSession()
        }
    }

    final def validateSession[A](bodyParser: BodyParser[A], block:Request[A] => InprogressApplication => Result):Action[A] = Action(bodyParser) {
      request =>
        request.getToken match {
          case Some(token) => isValidToken(token) match {
            case true => {
              val result = block(request)(request.getApplication)
              result.refreshSession()
            }
            case false => Redirect(routes.RegisterToVoteController.index()).withFreshSession()
          }
          case None => Redirect(routes.RegisterToVoteController.index()).withFreshSession()
        }
    }

    final def withParser[A](bodyParser: BodyParser[A]) = new {
      def storeAfter(action: Request[A] => InprogressApplication => (Result, InprogressApplication)) = validateSessionAndStore(bodyParser, action)
      def requiredFor(action: Request[A] => InprogressApplication => Result) = validateSession(bodyParser, action)
    }

    final def storeAfter(action: Request[AnyContent] => InprogressApplication => (Result, InprogressApplication)) = withParser(BodyParsers.parse.anyContent) storeAfter action

    final def requiredFor(action: Request[AnyContent] => InprogressApplication => Result) = withParser(BodyParsers.parse.anyContent) requiredFor action

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
    def getApplication = {
      request.cookies.get(sessionPayloadKey) match {
        case Some(cookie) => {
          val payloadKeyCookie = request.cookies.get(payloadCookieKeyParam)
          if (payloadKeyCookie.isDefined) {
            val decryptedInfo = encryptionService.decrypt(cookie.value, payloadKeyCookie.get.value ,encryptionKeys.cookies.getPrivate)
            serialiser.fromJson[InprogressApplication](decryptedInfo)
          }
          else InprogressApplication()
        }
        case _ => InprogressApplication()
      }
    }
  }

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

    def refreshSessionAndStore(application:InprogressApplication) = {
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

  trait SessionKeys {
    val sessionPayloadKey = "application"
    val sessionTokenKey = "sessionKey"

    val sessionTokenCookieKeyParam = "sessionTokenCookieKey"
    val payloadCookieKeyParam = "payloadCookieKey"
  }
}
