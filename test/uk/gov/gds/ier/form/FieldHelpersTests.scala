package uk.gov.gds.ier.form

import org.specs2.mutable.Specification
import org.specs2.matcher.Matchers

class FieldHelpersTests extends Specification with Matchers {
  "argsMapWithFilter.without" should {
    "exclude a given key in its output" in {
      val testArgs = Map('foo -> "foo")
      val outputArgs = FieldHelpers.argsMapWithFilter(testArgs).without('foo)

      outputArgs should not have key('foo)
    }
    "include all other keys" in {
      val testArgs = Map('foo -> "foo", 'bar -> "bar")
      val outputArgs = FieldHelpers.argsMapWithFilter(testArgs).without('bar)

      outputArgs should have key('foo)
      outputArgs should not have key('bar)
    }
    "filter out keys starting with _" in {
      val testArgs = Map('foo -> "foo", 'bar -> "bar", '_baz -> "baz")
      val outputArgs = FieldHelpers.argsMapWithFilter(testArgs).without('bar)

      outputArgs should have key('foo)
      outputArgs should not have key('bar)
      outputArgs should not have key('_baz)
    }
  }
}
