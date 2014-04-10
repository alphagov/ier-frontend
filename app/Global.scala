import uk.gov.gds.ier.client._
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.DynamicGlobal
import uk.gov.gds.ier.filter.{ResultFilter, StatsdFilter}
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.service.apiservice.{ConcreteIerApiService, IerApiService}
import uk.gov.gds.ier.stubs.{PlacesStubApiClient, LocateStubApiClient, IerStubApiClient, IerApiServiceWithStripNino}
import play.api.mvc._


object Global extends DynamicGlobal with Logging {

  override def bindings = {
    binder =>
      val config = new Config
      config.logConfiguration()
      if (config.fakeIer) {
        logger.debug("Binding IerStubApiClient")
        binder.bind(classOf[IerApiClient]).to(classOf[IerStubApiClient])
      }
      if (config.fakePlaces) {
        logger.debug("Binding PlacesStubApiClient")
        binder.bind(classOf[PlacesApiClient]).to(classOf[PlacesStubApiClient])
      }
      if (config.fakeLocate) {
        logger.debug("Binding LocateStubApiClient")
        binder.bind(classOf[LocateApiClient]).to(classOf[LocateStubApiClient])
      }
      if (config.stripNino) {
        logger.debug("Binding IerApiServiceWithStripNino")
        binder.bind(classOf[IerApiService]).to(classOf[IerApiServiceWithStripNino])
      } else {
        logger.debug("Binding ConcreteIerApiService")
        binder.bind(classOf[IerApiService]).to(classOf[ConcreteIerApiService])
      }
  }

  override def doFilter(next: EssentialAction): EssentialAction = {
    Filters(super.doFilter(next), StatsdFilter, ResultFilter)
  }
}
