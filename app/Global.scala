import uk.gov.gds.common.config.Config
import uk.gov.gds.ier.client.{StubApiClient, ApiClient}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.DynamicGlobal
import uk.gov.gds.ier.guice.Delegate

object Global extends DynamicGlobal {
  override def bindings = {
    binder =>
      val config = new Config
      if (config.fakeApi) {
        println("Binding StubApiClient")
        binder.bind(classOf[ApiClient]).to(classOf[StubApiClient])
      }
  }
}
