package uk.gov.gds.ier.validation

import play.api.data.{Field, Form}
import play.api.templates.Html

case class Key(key:String) {
  def asId(value:String = "") = List(key.replace(".", "_"), value.replace(" ", "_")).filter(_.nonEmpty).mkString("_")
  def item(i:Int) = this.copy(s"$key[$i]")
}

trait FormKeys {

  lazy val keys = new Keys{}

  trait Keys {
    lazy val namespace = ""

    def prependNamespace(k:Key):Key = {
      if (namespace.nonEmpty) {
        k.copy(namespace + "." + k.key)
      } else {
        k
      }
    }

    lazy val country = prependNamespace(Key("country"))
    lazy val residence = prependNamespace(Key("residence"))

    lazy val nationality = prependNamespace(Key("nationality"))

    lazy val british = prependNamespace(Key("british"))
    lazy val irish = prependNamespace(Key("irish"))
    lazy val nationalities = prependNamespace(Key("nationalities"))
    lazy val hasOtherCountry = prependNamespace(Key("hasOtherCountry"))
    lazy val otherCountries = prependNamespace(Key("otherCountries"))
    lazy val noNationalityReason = prependNamespace(Key("noNationalityReason"))

    lazy val name = prependNamespace(Key("name"))
    lazy val previousName = prependNamespace(Key("previousName"))
    lazy val hasPreviousName = prependNamespace(Key("hasPreviousName"))
    lazy val firstName = prependNamespace(Key("firstName"))
    lazy val middleNames = prependNamespace(Key("middleNames"))
    lazy val lastName = prependNamespace(Key("lastName"))

    lazy val dob = prependNamespace(Key("dob"))
    lazy val noDob = prependNamespace(Key("noDob"))
    lazy val range = prependNamespace(Key("range"))
    lazy val reason = prependNamespace(Key("reason"))

    lazy val day = prependNamespace(Key("day"))
    lazy val month = prependNamespace(Key("month"))
    lazy val year = prependNamespace(Key("year"))

    lazy val nino = prependNamespace(Key("NINO"))
    lazy val noNinoReason = prependNamespace(Key("NoNinoReason"))

    lazy val address = prependNamespace(Key("address"))

    lazy val lineOne = prependNamespace(Key("lineOne"))
    lazy val lineTwo = prependNamespace(Key("lineTwo"))
    lazy val lineThree = prependNamespace(Key("lineThree"))
    lazy val city = prependNamespace(Key("city"))
    lazy val county = prependNamespace(Key("county"))
    lazy val postcode = prependNamespace(Key("postcode"))
    lazy val uprn = prependNamespace(Key("uprn"))
    lazy val manualAddress = prependNamespace(Key("manualAddress"))
    lazy val addressLine = prependNamespace(Key("addressLine"))

    lazy val previousAddress = prependNamespace(Key("previousAddress"))
    lazy val movedRecently = prependNamespace(Key("movedRecently"))
    lazy val findAddress = prependNamespace(Key("findAddress"))
    lazy val otherAddress = prependNamespace(Key("otherAddress"))
    lazy val hasOtherAddress = prependNamespace(Key("hasOtherAddress"))
    lazy val openRegister = prependNamespace(Key("openRegister"))
    lazy val postalVote = prependNamespace(Key("postalVote"))
    lazy val optIn = prependNamespace(Key("optIn"))
    lazy val deliveryMethod = prependNamespace(Key("deliveryMethod"))
    lazy val methodName = prependNamespace(Key("methodName"))
    lazy val emailAddress = prependNamespace(Key("emailAddress"))

    lazy val contact = prependNamespace(Key("contact"))
    lazy val contactType = prependNamespace(Key("contactType"))
    lazy val contactMe = prependNamespace(Key("contactMe"))
    lazy val detail = prependNamespace(Key("detail"))
    lazy val email = prependNamespace(Key("email"))
    lazy val textNum = prependNamespace(Key("textNum"))
    lazy val phone = prependNamespace(Key("phone"))
    lazy val post = prependNamespace(Key("post"))

    lazy val possibleAddresses = prependNamespace(Key("possibleAddresses"))
    lazy val jsonList = prependNamespace(Key("jsonList"))

    lazy val previouslyRegistered = prependNamespace(Key("previouslyRegistered"))
    lazy val hasPreviouslyRegistered = prependNamespace(Key("hasPreviouslyRegistered"))

    lazy val lastUkAddress = prependNamespace(Key("lastUkAddress"))
    lazy val dateLeftUk = prependNamespace(Key("dateLeftUk"))
    lazy val lastRegisteredToVote = prependNamespace(Key("lastRegisteredToVote"))
    lazy val registeredType = prependNamespace(Key("registeredType"))

  }

  implicit class key2namespace(key:Key) extends Keys {
    override lazy val namespace = key.key
  }
  implicit class keys2Traversal(key:Key)(implicit formData:InProgressForm[uk.gov.gds.ier.model.InprogressOrdinary]) {
    def each(from:Int = 0)(block: (String, Int) => Html):Html = {
      val field = formData(key.item(from))
      field.value match {
        case Some(value) => block(field.name, from) += each(from+1)(block)
        case None => Html.empty
      }
    }
  }
}

object FormKeys extends FormKeys
