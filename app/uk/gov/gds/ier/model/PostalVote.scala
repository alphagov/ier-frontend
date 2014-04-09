package uk.gov.gds.ier.model

case class PostalVote (
    postalVoteOption: Option[Boolean],
    deliveryMethod: Option[PostalVoteDeliveryMethod]
)

object PostalVote extends ModelMapping {
  import playMappings._

  val mapping = playMappings.mapping(
    keys.optIn.key -> optional(boolean),
    keys.deliveryMethod.key -> optional(PostalVoteDeliveryMethod.mapping)
  ) (
    PostalVote.apply
  ) (
    PostalVote.unapply
  )
}
case class PostalVoteDeliveryMethod(
    deliveryMethod: Option[String],
    emailAddress: Option[String]
)

object PostalVoteDeliveryMethod extends ModelMapping {
  import playMappings._

  val mapping = playMappings.mapping(
    keys.methodName.key -> optional(nonEmptyText),
    keys.emailAddress.key -> optional(nonEmptyText)
  )(
    PostalVoteDeliveryMethod.apply
  )(
    PostalVoteDeliveryMethod.unapply
  )
}
