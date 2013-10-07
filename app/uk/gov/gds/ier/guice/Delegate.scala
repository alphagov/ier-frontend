package uk.gov.gds.ier.guice

import uk.gov.gds.guice.DependencyInjectionProvider

abstract class Delegate[A <: AnyRef](implicit m: Manifest[A]) extends DependencyInjectionProvider {
  protected lazy val delegate = dependency[A]
}
