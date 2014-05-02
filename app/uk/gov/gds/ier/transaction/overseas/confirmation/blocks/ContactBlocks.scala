package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import controllers.step.overseas.routes

trait ContactBlocks {
  self: ConfirmationBlock =>

  val post = if(form(keys.contact.post.contactMe).value == Some("true")){
    Some("By post")
  } else None

  val phone = if(form(keys.contact.phone.contactMe).value == Some("true")){
    Some(s"By phone: ${form(keys.contact.phone.detail).value.getOrElse("")}")
  } else None

  val email = if(form(keys.contact.email.contactMe).value == Some("true")){
    Some(s"By email: ${form(keys.contact.email.detail).value.getOrElse("")}")
  } else None

  def contact = {
    ConfirmationQuestion(
      title = "How we should contact you",
      editLink = routes.ContactController.editGet.url,
      changeName = "how we should contact you",
      content = ifComplete(keys.contact) {
        List(post, phone, email).flatten
      }
    )
  }
}
