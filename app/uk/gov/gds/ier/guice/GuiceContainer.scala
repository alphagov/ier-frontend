package uk.gov.gds.ier.guice

import com.google.inject.{Guice, Module, Injector, Binder, AbstractModule}

object GuiceContainer {

  private var di: Injector = null;

  /**
   * Call this method in a static context when your app starts up, within Global in play for example,
   * to initialize guice. You can pass in a module to configure the DI container if required, but this is optional.
   * If you don't do this then a default empty module will be used, which may be sufficient for most simple
   * applications.
   */

  def initialize(module: Module = new EmptyModule) = performInit(List(module))

  /**
   * Call this method in a static context when your app starts up, within Global in play for example,
   * to initialize guice. You can pass in an array of modules to configure the DI container. This method is provided
   * for the case when you want to initialise the DI container with an array of configuration modules.
   */

  def initialize(modules: List[Module]) = performInit(modules)

  def initialize(bindings: Binder => Unit) = performInit(
    List(new EmptyModule {
      override def configure {
        bindings(binder)
      }
    })
  )

  /**
    * Call this method before stopping your application. Useful to ensure that
    * your dependancies have been unloaded, e.g. in tests.
    */
  def destroy() = {
    di = null
  }

  @inline private[guice] final def dependency[A <: AnyRef](implicit m: Manifest[A]) =
    injector.getInstance(m.runtimeClass.asInstanceOf[Class[A]])

  @inline private[guice] final def performInit(modules: List[Module]) {
    synchronized {
      di = Guice.createInjector(modules.toSeq: _*)
    }
  }

  @inline private final def injector =
    if (di == null)
      throw new IllegalStateException("Guice is not initialised. You must call initialize first!")
    else
      di

  private[guice] class EmptyModule extends AbstractModule {
    def configure {
      // no-op
    }
  }
}
