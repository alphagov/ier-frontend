package uk.gov.gds.ier

import play.api.{Application, GlobalSettings}
import java.lang.Class
import uk.gov.gds.ier.client.ApiClient
import uk.gov.gds.guice.GuiceContainer
import com.google.inject.{Binder, AbstractModule}

trait DynamicGlobal extends GlobalSettings {

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
}