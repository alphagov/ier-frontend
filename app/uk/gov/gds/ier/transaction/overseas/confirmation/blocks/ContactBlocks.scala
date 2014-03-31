package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import controllers.step.overseas.routes

trait ContactBlocks {
  self: ConfirmationBlock =>

  val post = if(form(keys.contact.post.contactMe).value == Some("true")){
    "<p>By post</p>"
  } else ""

  val phone = if(form(keys.contact.phone.contactMe).value == Some("true")){
    s"<p>By phone: ${form(keys.contact.phone.detail).value.getOrElse("")}</p>"
  } else ""

  val email = if(form(keys.contact.email.contactMe).value == Some("true")){
    s"<p>By email: ${form(keys.contact.email.detail).value.getOrElse("")}</p>"
  } else ""

  def contact = {
    Some(ConfirmationQuestion(
      title = "How we should contact you",
      editLink = routes.ContactController.editGet.url,
      changeName = "how we should contact you",
      content = ifComplete(keys.contact) {
        s"$post $phone $email"
      }
    ))
  }
}
