package uk.gov.gds.ier.validation

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{Matchers, FlatSpec}
import play.api.data.{FormError, Form}
import play.api.data.Forms._

class ErrorTransformerTests
  extends FlatSpec 
  with Matchers {
  
  behavior of "ErrorTransformer.transform" 
  
  it should "one global error, one keyed error for an error with no args" in {
    val testError = FormError("foo.bar", "foobar'd")
    val transformedErrors = new ErrorTransformer().transform(
      Form(single("", text)).withError(testError)
    )
    transformedErrors.errors.size should be(2)
    transformedErrors.errors should contain(testError)
    transformedErrors.errors should contain(testError.copy(key = ""))
  }
  
  it should "one global error, one keyed error for an error with one key" in {
    val testError = FormError("foo.bar", "foobar'd", Key("foo.bar.baz") :: Nil)
    val transformedErrors = new ErrorTransformer().transform(
      Form(single("", text)).withError(testError)
    )
    transformedErrors.errors.size should be(2)
    transformedErrors.errors should contain(testError.copy(key = "foo.bar.baz"))
    transformedErrors.errors should contain(testError.copy(key = ""))
  }
  
  it should "one global error, two keyed errors for an error with two keys" in {
    val testError = FormError("foo.bar", "foobar'd", Key("foo.bar.baz") :: Key("foo.bar.boz") :: Nil)
    val transformedErrors = new ErrorTransformer().transform(
      Form(single("", text)).withError(testError)
    )
    transformedErrors.errors.size should be(3)
    transformedErrors.errors should contain(testError.copy(key = "foo.bar.baz"))
    transformedErrors.errors should contain(testError.copy(key = "foo.bar.boz"))
    transformedErrors.errors should contain(testError.copy(key = ""))
  }

  it should "two global errors, 2 keyed errors for an two error with one key" in {
    val testError1 = FormError("foo.bar.one", "one foobar'd", Key("foo.bar.baz") :: Nil)
    val testError2 = FormError("foo.bar.two", "two foobar'd", Key("foo.bar.boz") :: Nil)
    val transformedErrors = new ErrorTransformer().transform(
      Form(single("", text)).withError(testError1).withError(testError2)
    )
    transformedErrors.errors.size should be(4)
    transformedErrors.errors should contain(testError1.copy(key = "foo.bar.baz"))
    transformedErrors.errors should contain(testError1.copy(key = ""))
    transformedErrors.errors should contain(testError2.copy(key = "foo.bar.boz"))
    transformedErrors.errors should contain(testError2.copy(key = ""))
  }

  it should "no errors at all for no errors" in {
    val transformedErrors = new ErrorTransformer().transform(Form(single("", text)))
    transformedErrors.errors.size should be(0)
  }
}
