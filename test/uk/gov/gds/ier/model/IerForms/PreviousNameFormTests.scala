package uk.gov.gds.ier.model.IerForms

import play.api.libs.json.{JsNull, Json}
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.IerForms
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PreviousNameFormTests extends FlatSpec with Matchers with IerForms with WithSerialiser {

  val serialiser = new JsonSerialiser

  def toJson(obj: AnyRef): String = serialiser.toJson(obj)

  def fromJson[T](json: String)(implicit m: Manifest[T]): T = serialiser.fromJson(json)

  it should "successfully bind" in {
    val js = Json.toJson(
      Map(
        "previousName.hasPreviousName" -> "true",
        "previousName.previousName.firstName" -> "John",
        "previousName.previousName.middleNames" -> "Joe",
        "previousName.previousName.lastName" -> "Smith"
      )
    )
    previousNameForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.errorsAsMap)),
      success => {
        success.previousName.isDefined should be(true)
        val previousNameWrapper = success.previousName.get
        previousNameWrapper.hasPreviousName should be(true)
        previousNameWrapper.previousName.isDefined should be(true)

        val previousName = previousNameWrapper.previousName.get
        previousName.firstName should be("John")
        previousName.lastName should be("Smith")
        previousName.middleNames should be(Some("Joe"))
      }
    )
  }

  it should "successfully bind with no previous name" in {
    val js = Json.toJson(
      Map(
        "previousName.hasPreviousName" -> "false"
      )
    )
    previousNameForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.errorsAsMap)),
      success => {
        success.previousName.isDefined should be(true)
        val previousName = success.previousName.get
        previousName.hasPreviousName should be (false)
        previousName.previousName should be(None)
      }
    )
  }

  it should "error out if previousName is not provided" in {
    val js = Json.toJson(
      Map(
        "previousName.hasPreviousName" -> "true"
      )
    )
    previousNameForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorsAsMap.get("previousName") should be(Some(Seq("Please enter your previous name")))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out with no json" in {
    val js = JsNull
    previousNameForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorsAsMap.get("previousName") should be(Some(Seq("Please answer this question")))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on missing fields" in {
    val js = Json.toJson(
      Map(
        "previousName.hasPreviousName" -> "true",
        "previousName.previousName.firstName" -> "",
        "previousName.previousName.middleNames" -> "Joe",
        "previousName.previousName.lastName" -> ""
      )
    )
    previousNameForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorsAsMap.get("previousName.previousName") should be(Some(Seq("Please enter your full name")))
      },
      success => fail("Should have errorred out")
    )
  }
}
