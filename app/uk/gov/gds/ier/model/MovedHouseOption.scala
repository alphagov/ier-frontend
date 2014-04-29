package uk.gov.gds.ier.model

sealed case class MovedHouseOption(hasPreviousAddress:Boolean, name:String)

object MovedHouseOption extends ModelMapping {
  import playMappings._

  val Yes = MovedHouseOption(true, "yes")
  val MovedFromUk = MovedHouseOption(true, "from-uk")
  val MovedFromAbroad = MovedHouseOption(true, "from-abroad")
  val NotMoved = MovedHouseOption(false, "no")
  val DontKnow = MovedHouseOption(false, "dunno")
  val MovedFromAbroadRegistered = MovedHouseOption(true, "from-abroad-registered")
  val MovedFromAbroadNotRegistered = MovedHouseOption(true, "from-abroad-not-registered")

  def isValid(str:String):Boolean = {
    str match {
      case MovedFromUk.`name`|MovedFromAbroad.`name`|NotMoved.`name`|Yes.`name`|MovedFromAbroadRegistered.`name`|MovedFromAbroadNotRegistered.`name` => true
      case _ => false
    }
  }

  def parse(str:String):MovedHouseOption = {
    str match {
      case Yes.`name` => Yes
      case MovedFromUk.`name` => MovedFromUk
      case MovedFromAbroad.`name` => MovedFromAbroad
      case NotMoved.`name` => NotMoved
      case MovedFromAbroadRegistered.`name` => MovedFromAbroadRegistered
      case MovedFromAbroadNotRegistered.`name` => MovedFromAbroadNotRegistered
      case _ => DontKnow
    }
  }

  lazy val mapping = text.verifying(
    str => MovedHouseOption.isValid(str)
  ).transform[MovedHouseOption](
    str => MovedHouseOption.parse(str),
    option => option.name
  ).verifying(
    movedHouseUkAbroadOrNoOnly
  )

  lazy val movedHouseUkAbroadOrNoOnly = Constraint[MovedHouseOption]("movedHouse") {
    case MovedHouseOption.MovedFromUk => Valid
    case MovedHouseOption.MovedFromAbroad => Valid
    case MovedHouseOption.NotMoved => Valid
    case MovedHouseOption.MovedFromAbroadRegistered => Valid
    case MovedHouseOption.MovedFromAbroadNotRegistered => Valid
    case _ => Invalid("Not a valid option")
  }
}
