package uk.gov.gds.ier.model.IerForms

import play.api.libs.json.{JsNull, Json}
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{ErrorTransformer, IerForms}
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ContactFormTests extends FlatSpec with Matchers with IerForms with WithSerialiser {

  val serialiser = new JsonSerialiser

  def toJson(obj: AnyRef): String = serialiser.toJson(obj)

  def fromJson[T](json: String)(implicit m: Manifest[T]): T = serialiser.fromJson(json)

  it should "bind successfully (post)" in {
    val js = Json.toJson(
      Map(
        "contact.post" -> "123 Fake Street, SW1A 1AA",
        "contact.contactType" -> "post"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.errorsAsMap)),
      success => {
        success.contact.isDefined should be(true)
        val contact = success.contact.get
        contact.post should be(Some("123 Fake Street, SW1A 1AA"))
        contact.phone should be(None)
        contact.email should be(None)
        contact.textNum should be(None)
      }
    )
  }

  it should "bind successfully with no address (post)" in {
    val js = Json.toJson(
      Map(
        "contact.contactType" -> "post"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.errorsAsMap)),
      success => {
        success.contact.isDefined should be(true)
        val contact = success.contact.get
        contact.post should be(None)
        contact.phone should be(None)
        contact.email should be(None)
        contact.textNum should be(None)
      }
    )
  }

  it should "bind successfully (phone)" in {
    val js = Json.toJson(
      Map(
        "contact.phone" -> "1234567890",
        "contact.contactType" -> "phone"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.errorsAsMap)),
      success => {
        success.contact.isDefined should be(true)
        val contact = success.contact.get
        contact.phone should be(Some("1234567890"))
        contact.post should be(None)
        contact.email should be(None)
        contact.textNum should be(None)
      }
    )
  }

  it should "bind successfully (email)" in {
    val js = Json.toJson(
      Map(
        "contact.email" -> "fake@fake.com",
        "contact.contactType" -> "email"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.errorsAsMap)),
      success => {
        success.contact.isDefined should be(true)
        val contact = success.contact.get
        contact.email should be(Some("fake@fake.com"))
        contact.phone should be(None)
        contact.post should be(None)
        contact.textNum should be(None)
      }
    )
  }

  it should "bind successfully (textNum)" in {
    val js = Json.toJson(
      Map(
        "contact.textNum" -> "1234567890",
        "contact.contactType" -> "text"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.errorsAsMap)),
      success => {
        success.contact.isDefined should be(true)
        val contact = success.contact.get
        contact.textNum should be(Some("1234567890"))
        contact.phone should be(None)
        contact.post should be(None)
        contact.email should be(None)
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    contactForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorsAsMap.get("contact") should be(Some(Seq("Please answer this question")))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out on empty values" in {
    val js = Json.toJson(
      Map(
        "contact.post" -> "",
        "contact.phone" -> "",
        "contact.email" -> "",
        "contact.textNum" -> "",
        "contact.contactType" -> ""
      )
    )
    contactForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorsAsMap.get("contact") should be(Some(Seq("Please answer this question")))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out with contactType and no detail provided (phone)" in {
    val js = Json.toJson(
      Map(
        "contact.contactType" -> "phone"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorsAsMap.get("contact") should be(Some(Seq("Please enter your phone number")))
        new ErrorTransformer().transform(hasErrors).errorsAsMap.get("contact.phone") should be(Some(Seq("Please enter your phone number")))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out with contactType and no detail provided (email)" in {
    val js = Json.toJson(
      Map(
        "contact.contactType" -> "email"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorsAsMap.get("contact") should be(Some(Seq("Please enter your email address")))
        new ErrorTransformer().transform(hasErrors).errorsAsMap.get("contact.email") should be(Some(Seq("Please enter your email address")))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out with contactType and no detail provided (textNum)" in {
    val js = Json.toJson(
      Map(
        "contact.contactType" -> "text"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorsAsMap.get("contact") should be(Some(Seq("Please enter your phone number")))
        new ErrorTransformer().transform(hasErrors).errorsAsMap.get("contact.textNum") should be(Some(Seq("Please enter your phone number")))
      },
      success => fail("Should have thrown an error")
    )
  }
}
