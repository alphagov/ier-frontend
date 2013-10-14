package uk.gov.gds.ier.validation

trait FormKeys {
  lazy val namespace = ""

  def prependNamespace(str:String):String = {
    if (namespace.nonEmpty) {
      namespace + "." + str
    } else {
      str
    }
  }

  lazy val nationality = prependNamespace("nationality")

  lazy val nationalities = prependNamespace("nationalities")
  lazy val hasOtherCountry = prependNamespace("hasOtherCountry")
  lazy val otherCountries = prependNamespace("otherCountries")
  lazy val noNationalityReason = prependNamespace("noNationalityReason")

  lazy val name = prependNamespace("name")
  lazy val previousName = prependNamespace("previousName")
  lazy val hasPreviousName = prependNamespace("hasPreviousName")
  lazy val firstName = prependNamespace("firstName")
  lazy val middleNames = prependNamespace("middleNames")
  lazy val lastName = prependNamespace("lastName")

  lazy val dob = prependNamespace("dob")

  lazy val day = prependNamespace("day")
  lazy val month = prependNamespace("month")
  lazy val year = prependNamespace("year")

  lazy val nino = prependNamespace("NINO")
  lazy val noNinoReason = prependNamespace("NoNinoReason")

  lazy val address = prependNamespace("address")
  lazy val postcode = prependNamespace("postcode")
  lazy val previousAddress = prependNamespace("previousAddress")
  lazy val movedRecently = prependNamespace("movedRecently")
  lazy val otherAddress = prependNamespace("otherAddress")
  lazy val hasOtherAddress = prependNamespace("hasOtherAddress")
  lazy val openRegister = prependNamespace("openRegister")
  lazy val postalVote = prependNamespace("postalVote")
  lazy val optIn = prependNamespace("optIn")

  lazy val contact = prependNamespace("contact")
  lazy val contactType = prependNamespace("contactType")
  lazy val email = prependNamespace("email")
  lazy val textNum = prependNamespace("textNum")
  lazy val phone = prependNamespace("phone")
  lazy val post = prependNamespace("post")

  def item(i:Int) = namespace + "[" + i + "]"

  implicit class key2namespace(key:String) extends FormKeys {
    override lazy val namespace = key
  }

  implicit class Key2Id(key:String) {
    def asId = key.replace(".", "_")
  }
}

object FormKeys extends FormKeys
