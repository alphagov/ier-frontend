package uk.gov.gds.ier.guice

import org.scalatest.{FlatSpec, Matchers}
import com.google.inject.{AbstractModule, Binder}

class GuiceContainerTests
  extends FlatSpec
  with Matchers {

  it should "resolve a dependancy once initialised" in {
    val foo = new Foo

    GuiceContainer.initialize { binder =>
      binder bind classOf[Foo] toInstance foo
    }

    val dependency = GuiceContainer.dependency[Foo]
    dependency should be(foo)
    dependency.foo should be("foo")

    GuiceContainer.destroy
  }

  it should "not resolve a dependency once destroyed" in {
    val foo = new Foo

    GuiceContainer.initialize { binder =>
      binder bind classOf[Foo] toInstance foo
    }

    GuiceContainer.destroy

    intercept[IllegalStateException] {
      GuiceContainer.dependency[Foo]
    }
  }

  it should "resolve the correct dependency when destroyed and reinitialised" in {
    val foo = new Foo

    GuiceContainer.initialize { binder =>
      binder bind classOf[Foo] toInstance foo
    }

    GuiceContainer.dependency[Foo] should be(foo)

    GuiceContainer.destroy

    val notFoo = new Foo {
      override def foo = "bar"
    }

    GuiceContainer.initialize { binder =>
      binder bind classOf[Foo] toInstance notFoo
    }

    val dependency = GuiceContainer.dependency[Foo]
    dependency should not be(foo)
    dependency should be(notFoo)

    dependency.foo should be("bar")
  }
}

class Foo {
  def foo = "foo"
}
