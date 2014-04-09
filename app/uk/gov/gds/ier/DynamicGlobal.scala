package uk.gov.gds.ier

import uk.gov.gds.guice.GuiceContainer
import com.google.inject.{Binder, AbstractModule}
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.mustache.ErrorPageMustache
import uk.gov.gds.ier.config.Config
import scala.concurrent.Future
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import org.slf4j.MDC

trait DynamicGlobal 
    extends GlobalSettings 
    with Logging 
    with ErrorPageMustache {

  def bindings: Binder => Unit = { binder => }

  override def onStart(app: Application) {
    super.onStart(app)
    GuiceContainer.initialize(List(new AbstractModule() {
      @Override
      protected def configure() {
        bindings(binder())
      }
    }))
  }

  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    logger.debug(s"routing request ${request.method} ${request.path}")
    MDC.put("clientip", request.headers.get("X-Real-IP").getOrElse("N/A"))
    super.onRouteRequest(request)
  }

  override def onHandlerNotFound(request: RequestHeader) = {
    Future.successful {
      NotFound(ErrorPage.NotFound(request.path))
    }
  }

  override def onError(request: RequestHeader, ex: Throwable) = {
    logger.error(s"uncaught exception when routing request ${request.method} ${request.path}", ex)
    Future.successful {
      InternalServerError(ErrorPage.ServerError())
    }
  }
}
