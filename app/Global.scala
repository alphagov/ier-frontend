import uk.gov.gds.common.config.Config
import uk.gov.gds.ier.client._
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.DynamicGlobal
import uk.gov.gds.ier.guice.Delegate

object Global extends DynamicGlobal {
  override def bindings = {
    binder =>
      val config = new Config
      if (config.fakeIer) {
        println("Binding IerStubApiClient")
        binder.bind(classOf[IerApiClient]).to(classOf[IerStubApiClient])
      }
      if (config.fakePlaces) {
        println("Binding PlacesStubApiClient")
        binder.bind(classOf[PlacesApiClient]).to(classOf[PlacesStubApiClient])
      }
  }
}
