package uk.gov.gds.ier.validation

import org.scalatest.{Matchers, FlatSpec}
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Valid, Invalid, Constraint}
import uk.gov.gds.ier.test.TestHelpers

class TransformedFormTests
  extends FlatSpec
  with Matchers
  with TestHelpers {

  it should "validate like a form" in {
    val data = Map("foo" -> "john")
    val form = Form(single("foo" -> number))
    val boundForm = form.bind(data)

    val transformedForm = ErrorTransformForm(form)
    val boundTransformedForm = transformedForm.bind(data)
    boundTransformedForm.errors shouldNot be(boundForm.errors)
    boundTransformedForm.error("foo") should be(boundForm.error("foo"))
    boundTransformedForm.globalErrorMessages should be(Seq("error.number"))
  }

  it should "transform any errors that have Keys as args" in {
    lazy val constraint = Constraint[String] ("test constraint") {
      str => if (str == "John") Valid else Invalid("Not John", Key("foo.notJohn"))
    }
    val data = Map("foo" -> "jim")
    val form = Form(single("foo" -> text) verifying constraint)
    val boundForm = form.bind(data)

    val transformedForm = ErrorTransformForm(form)
    val boundTransformedForm = transformedForm.bind(data)
    boundTransformedForm.errors shouldNot be(boundForm.errors)
    boundTransformedForm.errorMessages("foo.notJohn") should be(Seq("Not John"))
    boundTransformedForm.globalErrorMessages should be(Seq("Not John"))
  }

  it should "not transform any errors that have Keys as args" in {
    lazy val constraint = Constraint[String] ("test constraint") {
      str => if (str == "John") Valid else Invalid("Not John")
    }
    val data = Map("foo" -> "jim")
    val form = Form(single("foo" -> text) verifying constraint)
    val boundForm = form.bind(data)

    val transformedForm = ErrorTransformForm(form)
    val boundTransformedForm = transformedForm.bind(data)
    boundTransformedForm.globalErrorMessages shouldNot be(Seq("Not John"))
  }
}
