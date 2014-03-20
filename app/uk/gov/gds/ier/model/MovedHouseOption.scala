package uk.gov.gds.ier.model

sealed case class MovedHouseOption(hasPreviousAddress:Boolean, name:String)
object MovedHouseOption {
  val MovedFromUk = MovedHouseOption(true, "from-uk")
  val MovedFromAbroad = MovedHouseOption(true, "from-abroad")
  val NotMoved = MovedHouseOption(false, "not-moved")
  val DontKnow = MovedHouseOption(false, "dunno")

  def parse(str:String):MovedHouseOption = {
    str match {
      case MovedFromUk.`name` => MovedFromUk
      case MovedFromAbroad.`name` => MovedFromAbroad
      case NotMoved.`name` => NotMoved
      case _ => DontKnow
    }
  }
}
