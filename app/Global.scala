import play.api.mvc.EssentialAction
import scala.concurrent.ExecutionContext
import uk.gov.gds.ier.client._
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.DynamicGlobal
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.service.{ConcreteIerApiService, IerApiService}
import uk.gov.gds.ier.stubs.{PlacesStubApiClient, IerStubApiClient, IerApiServiceWithStripNino}
import ExecutionContext.Implicits.global

object Global extends DynamicGlobal with Logging {

  override def bindings = {
    binder =>
      val config = new Config
      if (config.fakeIer) {
        logger.debug("Binding IerStubApiClient")
        binder.bind(classOf[IerApiClient]).to(classOf[IerStubApiClient])
      }
      if (config.fakePlaces) {
        logger.debug("Binding PlacesStubApiClient")
        binder.bind(classOf[PlacesApiClient]).to(classOf[PlacesStubApiClient])
      }
      if (config.stripNino) {
        logger.debug("Binding IerApiServiceWithStripNino")
        binder.bind(classOf[IerApiService]).to(classOf[IerApiServiceWithStripNino])
      } else {
        logger.debug("Binding ConcreteIerApiService")
        binder.bind(classOf[IerApiService]).to(classOf[ConcreteIerApiService])
      }
  }

  override def doFilter(action: EssentialAction): EssentialAction = EssentialAction { request =>
    action.apply(request).map(_.withHeaders(
      "X-Frame-Options" -> "deny"
    ))
  }
}
