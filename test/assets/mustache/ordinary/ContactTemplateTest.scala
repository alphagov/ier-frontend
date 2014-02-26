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
        title = "nationality title"
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

      val emailCheckBox = doc.select("input[id=contact_email_contactMe]").first()
      emailCheckBox.attr("id") should be("contact_email_contactMe")
      emailCheckBox.attr("name") should be("contact.email.contactMe")
      emailCheckBox.attr("value") should be("true")

      val phoneCheckBox = doc.select("input[id=contact_phone_contactMe]").first()
      phoneCheckBox.attr("id") should be("contact_phone_contactMe")
      phoneCheckBox.attr("name") should be("contact.phone.contactMe")
      phoneCheckBox.attr("value") should be("true")
      
      val postCheckBox = doc.select("input[id=contact_post_contactMe]").first()
      postCheckBox.attr("id") should be("contact_post_contactMe")
      postCheckBox.attr("name") should be("contact.post.contactMe")
      postCheckBox.attr("value") should be("true")
      
      val emailField = doc.select("input[id=contact_email_detail]").first()
      emailField.attr("id") should be("contact_email_detail")
      emailField.attr("name") should be("contact.email.detail")
      emailField.attr("value") should be("test@test.com")
      
      val phoneField = doc.select("input[id=contact_phone_detail]").first()
      phoneField.attr("id") should be("contact_phone_detail")
      phoneField.attr("name") should be("contact.phone.detail")
      phoneField.attr("value") should be("123456")
    }
  }
}