package uk.gov.gds.ier.model.IerForms

import play.api.libs.json.{JsNull, Json}
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.IerForms
import uk.gov.gds.ier.serialiser.JsonSerialiser
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ContactFormTests extends FlatSpec with Matchers with IerForms {

  val serialiser = new JsonSerialiser

  it should "bind successfully (post)" in {
    val js = Json.toJson(
      Map(
        "contact.post.detail" -> "123 Fake Street, SW1A 1AA",
        "contact.post.contactMe" -> "true"
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

  it should "bind successfully (phone)" in {
    val js = Json.toJson(
      Map(
        "contact.phone.detail" -> "1234567890",
        "contact.phone.contactMe" -> "true"
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
        "contact.email.detail" -> "fake@fake.com",
        "contact.email.contactMe" -> "true"
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
        "contact.textNum.detail" -> "1234567890",
        "contact.textNum.contactMe" -> "true"
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
        "contact.post.detail" -> "",
        "contact.post.contactMe" -> "",
        "contact.phone.detail" -> "",
        "contact.phone.contactMe" -> "",
        "contact.email.detail" -> "",
        "contact.email.contactMe" -> "",
        "contact.textNum.detail" -> "",
        "contact.textNum.contactMe" -> ""
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

  it should "error out with contactMe=true and no detail provided (except post)" in {
    val js = Json.toJson(
      Map(
        "contact.post.contactMe" -> "true",
        "contact.email.contactMe" -> "true",
        "contact.phone.contactMe" -> "true",
        "contact.textNum.contactMe" -> "true"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(4)
        hasErrors.errorsAsMap.get("contact.email") should be(Some(Seq("Please enter your email address")))
        hasErrors.errorsAsMap.get("contact.post") should be(Some(Seq("Please enter an address")))
        hasErrors.errorsAsMap.get("contact.phone") should be(Some(Seq("Please enter your phone number")))
        hasErrors.errorsAsMap.get("contact.textNum") should be(Some(Seq("Please enter your phone number")))
      },
      success => fail("Should have thrown an error")
    )
  }
}
