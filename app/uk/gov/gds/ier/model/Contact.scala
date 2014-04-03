package uk.gov.gds.ier.model

case class Contact (post: Boolean,
                    phone: Option[ContactDetail],
                    email: Option[ContactDetail]) {

  def toApiMap = {
    Map("post" -> post.toString) ++
      phone.filter(_.contactMe).flatMap(_.detail).map("phone" -> _).toMap ++
      email.filter(_.contactMe).flatMap(_.detail).map("email" -> _).toMap
  }
}

case class ContactDetail (contactMe:Boolean,
                          detail:Option[String])