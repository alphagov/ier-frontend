import uk.gov.gds.ier.client._
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.DynamicGlobal
import uk.gov.gds.ier.feedback.{FeedbackClient, FeedbackClientImpl}
import uk.gov.gds.ier.filter.{AssetsCacheFilter, ResultFilter, StatsdFilter}
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.service.apiservice.{ConcreteIerApiService, IerApiService}
import uk.gov.gds.ier.stubs.{FeedbackStubClient, IerApiServiceWithStripNino, IerStubApiClient, LocateStubApiClient}
import play.api.mvc._
import com.kenshoo.play.metrics.{MetricsFilter, MetricsRegistry, MetricsController}
import java.util.concurrent.TimeUnit
import java.net.InetSocketAddress

import com.codahale.metrics.{ConsoleReporter, MetricFilter}
import com.codahale.metrics.graphite.{Graphite, GraphiteReporter}


object Global extends DynamicGlobal with Logging {

  override def bindings = {
    binder =>
      val config = new Config
      if (config.fakeIer) {
        logger.debug("Binding IerStubApiClient")
        binder.bind(classOf[IerApiClient]).to(classOf[IerStubApiClient])
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

      if (config.fakeFeedbackService) {
        logger.debug("Binding FeedbackStubClient")
        binder.bind(classOf[FeedbackClient]).to(classOf[FeedbackStubClient])
      } else {
        logger.debug("Binding FeedbackClientImpl")
        binder.bind(classOf[FeedbackClient]).to(classOf[FeedbackClientImpl])
      }
  }

  override def doFilter(next: EssentialAction): EssentialAction = {
    Filters(super.doFilter(next), StatsdFilter, ResultFilter, new AssetsCacheFilter(remoteAssets))
  }
}

object GlobalMetrics {

  val metricRegistry = new com.codahale.metrics.MetricRegistry()

  val hostedGraphiteService = new Graphite(new InetSocketAddress("carbon.hostedgraphite.com", 2003))
  val apiKey = "<API-KEY>"

  val graphiteReporter = GraphiteReporter.forRegistry(metricRegistry)
    .prefixedWith(apiKey)
    .convertRatesTo(TimeUnit.SECONDS)
    .convertDurationsTo(TimeUnit.MILLISECONDS)
    .filter(MetricFilter.ALL)
    .build(hostedGraphiteService)

}


