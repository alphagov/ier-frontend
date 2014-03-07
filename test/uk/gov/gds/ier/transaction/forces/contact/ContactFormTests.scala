package uk.gov.gds.ier.transaction.forces.contact

import play.api.libs.json.{JsNull, Json}
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.ContactDetail

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
        "contact.email.contactMe" -> "true"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.contact.isDefined should be(true)
        val contact = success.contact.get
        contact.post should be(true)
        contact.phone should be(Some(ContactDetail(true, Some("1234567890"))))
        contact.email should be(Some(ContactDetail(true, Some("fake@fake.com"))))
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
        contact.phone should be(Some(ContactDetail(true,Some("1234567890"))))
        contact.post should be(false)
        contact.email should be(None)
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
        contact.email should be(Some(ContactDetail(true,Some("fake@fake.com"))))
        contact.phone should be(None)
        contact.post should be(false)
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
        "contact.post.detail" -> "",
        "contact.phone.detail" -> "",
        "contact.email.detail" -> ""
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

  it should "error out with contactType and invalid email provided" in {
    val js = Json.toJson(
      Map(
        "contact.email.contactMe" -> "true",
        "contact.email.detail" -> "test@mail"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("contact.email.detail") should be(Seq("Please enter a valid email address"))
        hasErrors.globalErrorMessages should be(Seq("Please enter a valid email address"))
      },
      success => fail("Should have thrown an error")
    )
  }

}
