package uk.gov.gds.ier.guice

/**
 * Mix this trait into any play controllers that require access to the dependency injection framework.
 *
 * If you wish to specify your own
 */
trait DependencyInjectionProvider {

  /**
   * This method will return a dependency from the dependency injection framework. It is designed to be used from
   * objects that are not managed by the dependency injection framework, such as play controllers. It only allows for
   * dependency lookup by class, which should be enough for all uses in controllers.
   *
   * @tparam A The class of the dependency to lookup
   * @return instance of class resolved from dependency injection framework
   */

  @inline protected final def dependency[A <: AnyRef](implicit m: Manifest[A]) = GuiceContainer.dependency[A]
}
