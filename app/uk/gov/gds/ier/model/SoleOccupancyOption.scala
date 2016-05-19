package uk.gov.gds.ier.model

sealed case class SoleOccupancyOption(soleOccupancy:Boolean, name:String)

object SoleOccupancyOption extends ModelMapping {
  import playMappings._

  val Yes = SoleOccupancyOption(false, "yes")
  val No = SoleOccupancyOption(true, "no")
  val NotSure = SoleOccupancyOption(false, "not-sure")
  val SkipThisQuestion = SoleOccupancyOption(false, "skip-this-question")

  def isValid(str:String):Boolean = {
    str match {
      case
        Yes.`name` |
        No.`name`|
        NotSure.`name` |
        SkipThisQuestion.`name` => true
      case _ => false
    }
  }

  def parse(str:String):SoleOccupancyOption = {
    str match {
      case Yes.`name` => Yes
      case No.`name` => No
      case NotSure.`name` => NotSure
      case SkipThisQuestion.`name` => SkipThisQuestion
    }
  }

  lazy val mapping = text.verifying(
    str => SoleOccupancyOption.isValid(str)
  ).transform[SoleOccupancyOption](
    str => SoleOccupancyOption.parse(str),
    option => option.name
  ).verifying(
    allPossibleMoveOptions
  )

  lazy val allPossibleMoveOptions = Constraint[SoleOccupancyOption]("soleOccupancy") {
    case SoleOccupancyOption.Yes => Valid
    case SoleOccupancyOption.No => Valid
    case SoleOccupancyOption.NotSure => Valid
    case SoleOccupancyOption.SkipThisQuestion => Valid

    case _ => Invalid("Not a valid option")
  }
}
