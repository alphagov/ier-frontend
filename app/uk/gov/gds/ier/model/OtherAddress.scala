package uk.gov.gds.ier.model

case class OtherAddress (otherAddressOption:OtherAddressOption) {
  def toApiMap = {
    Map("oadr" -> otherAddressOption.name)
  }
}

object OtherAddress {
  val NoOtherAddress = OtherAddressOption(false, "none")
  val StudentOtherAddress = OtherAddressOption(true, "student")
  val HomeOtherAddress = OtherAddressOption(true, "secondHome")

  def parse(str:String):OtherAddressOption = {
    str match {
      case "secondHome" => HomeOtherAddress
      case "student" => StudentOtherAddress
      case _ => NoOtherAddress
    }
  }
}

case class OtherAddressOption(hasOtherAddress:Boolean, name:String)