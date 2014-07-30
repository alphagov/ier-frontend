package uk.gov.gds.ier.model

sealed case class PostalVoteOption(postalVote:Boolean, name:String)

object PostalVoteOption extends ModelMapping {
  import playMappings._

  val Yes = PostalVoteOption(true, "yes")
  val NoAndVoteInPerson = PostalVoteOption(true, "no-vote-in-person")
  val NoAndAlreadyHave = PostalVoteOption(true, "no-already-have")

  def isValid(str:String):Boolean = {
    str match {
      case
        Yes.`name` |
        NoAndVoteInPerson.`name`|
        NoAndAlreadyHave.`name` => true
      case _ => false
    }
  }

  def parse(str:String):PostalVoteOption = {
    str match {
      case Yes.`name` => Yes
      case NoAndVoteInPerson.`name` => NoAndVoteInPerson
      case NoAndAlreadyHave.`name` => NoAndAlreadyHave
    }
  }

  lazy val mapping = text.verifying(
    str => PostalVoteOption.isValid(str)
  ).transform[PostalVoteOption](
      str => PostalVoteOption.parse(str),
      option => option.name
    ).verifying(
      allPossibleMoveOptions
    )

  lazy val allPossibleMoveOptions = Constraint[PostalVoteOption]("postalVote") {
    case PostalVoteOption.Yes => Valid
    case PostalVoteOption.NoAndVoteInPerson => Valid
    case PostalVoteOption.NoAndAlreadyHave => Valid

    case _ => Invalid("Not a valid option")
  }
}
