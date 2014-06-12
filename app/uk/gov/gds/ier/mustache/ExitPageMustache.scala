package uk.gov.gds.ier.mustache

import uk.gov.gds.ier.guice.{WithRemoteAssets, WithConfig}
import uk.gov.gds.ier.langs.Messages

trait ExitPageMustache extends StepMustache {
  self: WithRemoteAssets
    with WithConfig =>

  object ExitPages {

    case class BritishIslands(
        title: Option[String] = None
    ) (
        implicit val lang: Lang
    ) extends ArticlePage("exit/britishIslands")
      with MessagesForMustache {
      val pageTitle = title getOrElse Messages("exit_britishIslands_title")
    }

    case class DontKnow(
      title: Option[String] = None
    ) (
      implicit val lang: Lang
    ) extends ArticlePage("exit/dontKnow")
    with MessagesForMustache {
      val pageTitle = title getOrElse Messages("exit_dontKnow_title")
    }

    case class NoFranchise(
      title: Option[String] = None
    ) (
      implicit val lang: Lang
    ) extends ArticlePage("exit/noFranchise")
      with MessagesForMustache {
      val pageTitle = title getOrElse Messages("exit_nationality_noFranchise_title")
    }

    case class NorthernIreland(
        title: Option[String] = None
    ) (
        implicit val lang: Lang
    ) extends ArticlePage("exit/northernIreland")
      with MessagesForMustache {
      val pageTitle = title getOrElse Messages("exit_northernIreland_title")
    }

    case class Scotland (
      implicit val lang: Lang
    ) extends ArticlePage("exit/scotland")
      with MessagesForMustache {
      val pageTitle =  Messages("exit_scotland_title")
    }

    case class TooYoung (
        title: Option[String] = None
    ) (
        implicit val lang: Lang
    ) extends ArticlePage("exit/tooYoung")
      with MessagesForMustache {
      val pageTitle = title getOrElse Messages("exit_tooYoung_title")
    }

    case class Under18 (
        title: Option[String] = None
    ) (
        implicit val lang: Lang
    ) extends ArticlePage("exit/under18")
      with MessagesForMustache {
      val pageTitle = title getOrElse Messages("exit_under18_title")
    }

    case class LeftService (
        pageTitle: String = "Sorry, you cannot registers because it has been over 15 years since you left the position"
    ) extends ArticlePage("exit/leftService")

    case class LeftUk (
        pageTitle: String = "Sorry, you cannot registers because it has been over 15 years since you left the UK"
    ) extends ArticlePage("exit/leftUk")

    case class TooOldWhenLeft (
        pageTitle: String = "Sorry, you cannot register to vote overseas because you were not registered to vote when you lived in the UK"
    ) extends ArticlePage("exit/tooOldWhenLeft")
  }
}
