package assets.mustache.ordinary

import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import org.jsoup.Jsoup
import play.api.test.Helpers._
import uk.gov.gds.ier.transaction.ordinary.contact.ContactMustache

class ContactTemplateTest
  extends FlatSpec
  with ContactMustache
  with Matchers {

  it should "properly render all properties from the model" in {

    running(FakeApplication()) {
      val data = ContactModel(
        question = Question(postUrl = "/whatever-url",
        backUrl = "",
        number = "1",
        title = "contact title"
        ),
        contactFieldSet = FieldSet(
          classes = ""
        ),
        contactEmailCheckbox = Field(
          id = "contact_email_contactMe",
          name = "contact.email.contactMe",
          value = "true"
        ),
        contactPhoneCheckbox = Field(
          id = "contact_phone_contactMe",
          name = "contact.phone.contactMe",
          value = "true"
        ),
        contactPostCheckbox = Field(
          id = "contact_post_contactMe",
          name = "contact.post.contactMe",
          value = "true"
        ),
        contactEmailText = Field(
          id = "contact_email_detail",
          name = "contact.email.detail",
          value = "test@test.com"
        ),
        contactPhoneText = Field(
          id = "contact_phone_detail",
          name = "contact.phone.detail",
          value = "123456"
        )
      )

      val html = Mustache.render("ordinary/contact", data)
      val doc = Jsoup.parse(html.toString)
      
      // page
      val f = doc.select("form").first() 
      f should not be(null)
      f.attr("action") should be ("/whatever-url")

      val h = doc.select("header").first() 
      h should not be(null)
      h.text should include ("1")
      h.text should include ("contact title")

      val emailCheckBox = doc.select("input#contact_email_contactMe").first()
      emailCheckBox should not be (null)
      emailCheckBox.attr("id") should be("contact_email_contactMe")
      emailCheckBox.attr("name") should be("contact.email.contactMe")
      emailCheckBox.attr("value") should be("true")

      val phoneCheckBox = doc.select("input#contact_phone_contactMe").first()
      phoneCheckBox should not be (null)
      phoneCheckBox.attr("id") should be("contact_phone_contactMe")
      phoneCheckBox.attr("name") should be("contact.phone.contactMe")
      phoneCheckBox.attr("value") should be("true")
      
      val postCheckBox = doc.select("input#contact_post_contactMe").first()
      postCheckBox should not be (null)
      postCheckBox.attr("id") should be("contact_post_contactMe")
      postCheckBox.attr("name") should be("contact.post.contactMe")
      postCheckBox.attr("value") should be("true")
      
      val emailField = doc.select("input#contact_email_detail").first()
      emailField should not be (null)
      emailField.attr("id") should be("contact_email_detail")
      emailField.attr("name") should be("contact.email.detail")
      emailField.attr("value") should be("test@test.com")
      
      val phoneField = doc.select("input#contact_phone_detail").first()
      phoneField should not be (null)
      phoneField.attr("id") should be("contact_phone_detail")
      phoneField.attr("name") should be("contact.phone.detail")
      phoneField.attr("value") should be("123456")
    }
  }
}