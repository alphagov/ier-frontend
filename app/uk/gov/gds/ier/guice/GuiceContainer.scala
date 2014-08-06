package uk.gov.gds.ier.guice

import com.google.inject.{Guice, Module, Injector, Inject, Binder, AbstractModule}

class GuiceContainer @Inject() (val di: Injector) {

  @inline final def dependency[A <: AnyRef](implicit m: Manifest[A]) = {
    di.getInstance(m.runtimeClass.asInstanceOf[Class[A]])
  }

  @inline final def dependency[A](dependencyClass: Class[A]) = {
    di.getInstance(dependencyClass)
  }
}

object GuiceContainer {
  private[guice] class EmptyModule extends AbstractModule {
    def configure {/* no-op*/}
  }

  def apply(modules: List[Module]): GuiceContainer = {
    new GuiceContainer(Guice.createInjector(modules.toSeq: _*))
  }

  def apply(bindings: Binder => Unit): GuiceContainer = {
    GuiceContainer(List(new EmptyModule {
      override def configure {
        bindings(binder)
      }
    }))
  }
}
