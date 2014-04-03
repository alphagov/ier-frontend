package uk.gov.gds.ier.model

import scala.util.Try

sealed case class LastRegisteredType(name:String)

object LastRegisteredType {
  val Ordinary = LastRegisteredType("ordinary")
  val Forces = LastRegisteredType("forces")
  val Crown = LastRegisteredType("crown")
  val Council = LastRegisteredType("council")
  val NotRegistered = LastRegisteredType("not-registered")

  def isValid(str:String) = {
    Try {
      parse(str)
    }.isSuccess
  }

  def parse(str:String) = {
    str match {
      case "ordinary" => Ordinary
      case "forces" => Forces
      case "crown" => Crown
      case "council" => Council
      case "not-registered" => NotRegistered
      case _ => throw new IllegalArgumentException(s"$str not a valid LastRegisteredType")
    }
  }
}