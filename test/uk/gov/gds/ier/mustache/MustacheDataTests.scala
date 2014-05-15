package uk.gov.gds.ier.mustache

import uk.gov.gds.ier.test.{TestHelpers, CustomMatchers}
import org.scalatest.{Matchers, FlatSpec}
import play.api.i18n.{MessagesPlugin, Lang}
import play.api.test.FakeApplication

class MustacheDataTests
  extends FlatSpec
  with Matchers
  with CustomMatchers
  with TestHelpers {

  it should "default the lang to english if no implicit in scope" in {
    case class FooModel (
        question: Question,
        name: String
    ) extends MustacheData

    val foo = FooModel(
      question = Question(),
      name = "Foo"
    )

    foo.question.lang.language should be("en")
  }

  it should "take the lang from the implicit in scope" in {
    case class FooModel (
        question: Question,
        name: String
    ) extends MustacheData

    implicit val welsh = Lang("cy")

    val foo = FooModel(
      question = Question(),
      name = "Foo"
    )

    foo.question.lang.language should be("cy")
  }
}
