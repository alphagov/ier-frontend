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

trait SessionHandling {
  self: WithSerialiser with Controller =>

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
        block(request).withFreshSession()(request)
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
              result.refreshSessionAndStore(application)(request)
            }
            case false => Redirect(routes.RegisterToVoteController.index()).withFreshSession()(request)
          }
          case None => Redirect(routes.RegisterToVoteController.index()).withFreshSession()(request)
        }
    }

    final def validateSession[A](bodyParser: BodyParser[A], block:Request[A] => InprogressApplication => Result):Action[A] = Action(bodyParser) {
      request =>
        request.getToken match {
          case Some(token) => isValidToken(token) match {
            case true => {
              val result = block(request)(request.getApplication)
              result.refreshSession()(request)
            }
            case false => Redirect(routes.RegisterToVoteController.index()).withFreshSession()(request)
          }
          case None => Redirect(routes.RegisterToVoteController.index()).withFreshSession()(request)
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
        dt.isAfter(DateTime.now.minusMinutes(5))
      } catch {
        case e:IllegalArgumentException => false
      }
    }
  }

  private implicit class InProgressRequest(request:play.api.mvc.Request[_]) extends SessionKeys {
    def getToken = {
      val cookie = request.cookies.get(sessionTokenKey)
      if (cookie.isDefined) {
        val sessionTokenKey = request.session.get(sessionTokenCookieKeyParam).getOrElse("")
        val decryptedInfo = EncryptionService.decrypt(cookie.get.value, sessionTokenKey ,EncryptionKeys.cookies.getPrivate)
        Some(decryptedInfo)
      }
      else {
        None
      }
    }
    def getApplication = {
      request.cookies.get(sessionPayloadKey) match {
        case Some(cookie) => {
          val payloadKey = request.session.get(payloadCookieKeyParam).getOrElse("")
          val decryptedInfo = EncryptionService.decrypt(cookie.value, payloadKey ,EncryptionKeys.cookies.getPrivate)
          serialiser.fromJson[InprogressApplication](decryptedInfo)
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

    def withFreshSession()(implicit request: play.api.mvc.Request[_]) = {
      val (encryptedSessionTokenValue, sessionTokenCookieKey) = EncryptionService.encrypt(DateTime.now.toString(), EncryptionKeys.cookies.getPublic)
      val newSession = request.session + (sessionTokenCookieKeyParam -> sessionTokenCookieKey)
      result.withCookies(Cookie(sessionTokenKey, encryptedSessionTokenValue.filter(_ >= ' '))).discardingCookies(DiscardingCookie(sessionPayloadKey))
        .withSession(newSession)
    }

    def refreshSessionAndStore(application:InprogressApplication)(implicit request: play.api.mvc.Request[_]) = {
      val (encryptedSessionPayloadValue, payloadCookieKey) = EncryptionService.encrypt(serialiser.toJson(application), EncryptionKeys.cookies.getPublic)
      val (encryptedSessionTokenValue, sessionTokenCookieKey) = EncryptionService.encrypt(DateTime.now.toString(), EncryptionKeys.cookies.getPublic)
      val newSession = request.session + (payloadCookieKeyParam -> payloadCookieKey) + (sessionTokenCookieKeyParam -> sessionTokenCookieKey)
      result.withCookies(
        Cookie(sessionTokenKey, encryptedSessionTokenValue.filter(_ >= ' ')),
        Cookie(sessionPayloadKey, encryptedSessionPayloadValue.filter(_ >= ' ')))
        .withSession(newSession)
    }

    def refreshSession()(implicit request: play.api.mvc.Request[_]) = {
      val (encryptedSessionTokenValue, sessionTokenCookieKey) = EncryptionService.encrypt(DateTime.now.toString(), EncryptionKeys.cookies.getPublic)
      val newSession = request.session + (sessionTokenCookieKeyParam -> sessionTokenCookieKey)
      result.withCookies(Cookie(sessionTokenKey, encryptedSessionTokenValue.filter(_ >= ' ')))
        .withSession(newSession)
    }

    def createSecureCookie ( name : String, value : String) : Cookie = {
      Cookie (name, value)
      //Cookie (name, value, maxAge: Option[Int] = None, path: String = "/", domain: Option[String] = None, secure: Boolean = false, httpOnly: Boolean = true))
    }

  }

  trait SessionKeys {
    val sessionPayloadKey = "application"
    val sessionTokenKey = "sessionKey"

    val sessionTokenCookieKeyParam = "sessionTokenCookieKey"
    val payloadCookieKeyParam = "payloadCookieKey"
  }
}
