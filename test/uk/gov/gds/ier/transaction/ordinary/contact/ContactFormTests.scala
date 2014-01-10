package uk.gov.gds.ier.transaction.contact

import play.api.libs.json.{JsNull, Json}
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.serialiser.WithSerialiser

class ContactFormTests 
  extends FlatSpec
  with Matchers
  with ContactForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser
  
  it should "bind successfully (all)" in {
    val js = Json.toJson(
      Map(
        "contact.post.contactMe" -> "true",
        "contact.phone.detail" -> "1234567890",
        "contact.phone.contactMe" -> "true",
        "contact.email.detail" -> "fake@fake.com",
        "contact.email.contactMe" -> "true",
        "contact.textNum.detail" -> "1234567890",
        "contact.textNum.contactMe" -> "true"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.contact.isDefined should be(true)
        val contact = success.contact.get
        contact.post should be(true)
        contact.phone should be(Some("1234567890"))
        contact.email should be(Some("fake@fake.com"))
        contact.textNum should be(Some("1234567890"))
      }
    )
  }
  
  it should "bind successfully (post)" in {
    val js = Json.toJson(
      Map(
        "contact.post.contactMe" -> "true"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.contact.isDefined should be(true)
        val contact = success.contact.get
        contact.post should be(true)
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
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.contact.isDefined should be(true)
        val contact = success.contact.get
        contact.phone should be(Some("1234567890"))
        contact.post should be(false)
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
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.contact.isDefined should be(true)
        val contact = success.contact.get
        contact.email should be(Some("fake@fake.com"))
        contact.phone should be(None)
        contact.post should be(false)
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
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.contact.isDefined should be(true)
        val contact = success.contact.get
        contact.textNum should be(Some("1234567890"))
        contact.phone should be(None)
        contact.post should be(false)
        contact.email should be(None)
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    contactForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("contact") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out on empty values" in {
    val js = Json.toJson(
      Map(
        "contact.phone.contactMe" -> "",
        "contact.email.contactMe" -> "",
        "contact.textNum.contactMe" -> "",
        "contact.post.detail" -> "",
        "contact.phone.detail" -> "",
        "contact.email.detail" -> "",
        "contact.textNum.detail" -> ""
      )
    )
    contactForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("contact") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out with contactType and no detail provided (phone)" in {
    val js = Json.toJson(
      Map(
        "contact.phone.contactMe" -> "true"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("contact.phone.detail") should be(Seq("Please enter your phone number"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your phone number"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out with contactType and no detail provided (email)" in {
    val js = Json.toJson(
      Map(
        "contact.email.contactMe" -> "true"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("contact.email.detail") should be(Seq("Please enter your email address"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your email address"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out with contactType and no detail provided (textNum)" in {
    val js = Json.toJson(
      Map(
        "contact.textNum.contactMe" -> "true"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("contact.textNum.detail") should be(Seq("Please enter your phone number"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your phone number"))
      },
      success => fail("Should have thrown an error")
    )
  }
}
