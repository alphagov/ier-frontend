package uk.gov.gds.ier.model

case class PostalVote (postalVoteOption: Option[Boolean], deliveryMethod: Option[PostalVoteDeliveryMethod])

case class PostalVoteDeliveryMethod(deliveryMethod: Option[String], emailAddress: Option[String])