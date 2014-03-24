package uk.gov.gds.ier.model

sealed case class MovedHouseOption(hasPreviousAddress:Boolean, name:String)
object MovedHouseOption {
  val Yes = MovedHouseOption(true, "yes")
  val MovedFromUk = MovedHouseOption(true, "from-uk")
  val MovedFromAbroad = MovedHouseOption(true, "from-abroad")
  val NotMoved = MovedHouseOption(false, "no")
  val DontKnow = MovedHouseOption(false, "dunno")

  def isValid(str:String):Boolean = {
    str match {
      case MovedFromUk.`name`|MovedFromAbroad.`name`|NotMoved.`name`|Yes.`name` => true
      case _ => false
    }
  }
  def parse(str:String):MovedHouseOption = {
    str match {
      case Yes.`name` => Yes
      case MovedFromUk.`name` => MovedFromUk
      case MovedFromAbroad.`name` => MovedFromAbroad
      case NotMoved.`name` => NotMoved
      case _ => DontKnow
    }
  }
}
