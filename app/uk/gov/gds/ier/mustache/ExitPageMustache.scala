package uk.gov.gds.ier.mustache

import uk.gov.gds.ier.guice.WithRemoteAssets
import uk.gov.gds.ier.langs.Messages

trait ExitPageMustache extends StepMustache {
  self: WithRemoteAssets =>

  object ExitPages {
    abstract class ExitTemplate(template:String) extends Mustachio(template) {

      val pageTitle:String

      override def render() = GovukTemplate(
        mainContent = super.render(),
        pageTitle = pageTitle,
        contentClasses = "article"
      )
    }

    case class BritishIslands(
        title: Option[String] = None
    ) (
        implicit val lang: Lang
    ) extends ExitTemplate("exit/britishIslands")
      with MessagesForMustache {
      val pageTitle = title getOrElse Messages("exit_britishIslands_title")
    }

    case class DontKnow(
        pageTitle: String = "Register to Vote - You need to find out whether you're 18 or over"
    ) extends ExitTemplate("exit/dontKnow")

    case class NoFranchise(
        pageTitle: String = "Register to Vote - Sorry, you can’t register to vote"
    ) extends ExitTemplate("exit/noFranchise")

    case class NorthernIreland(
        title: Option[String] = None
    ) (
        implicit val lang: Lang
    ) extends ExitTemplate("exit/northernIreland")
      with MessagesForMustache {
      val pageTitle = title getOrElse Messages("exit_northernIreland_title")
    }

    case class Scotland (
      implicit val lang: Lang
    ) extends ExitTemplate("exit/scotland")
      with MessagesForMustache {
      val pageTitle =  Messages("exit_scotland_title")
    }

    case class TooYoung (
        title: Option[String] = None
    ) (
        implicit val lang: Lang
    ) extends ExitTemplate("exit/tooYoung")
      with MessagesForMustache {
      val pageTitle = title getOrElse Messages("exit_tooYoung_title")
    }

    case class Under18 (
        title: Option[String] = None
    ) (
        implicit val lang: Lang
    ) extends ExitTemplate("exit/under18")
      with MessagesForMustache {
      val pageTitle = title getOrElse Messages("exit_under18_title")
    }

    case class LeftService (
        pageTitle: String = "Sorry, you cannot registers because it has been over 15 years since you left the position"
    ) extends ExitTemplate("exit/leftService")

    case class LeftUk (
        pageTitle: String = "Sorry, you cannot registers because it has been over 15 years since you left the UK"
    ) extends ExitTemplate("exit/leftUk")

    case class TooOldWhenLeft (
        pageTitle: String = "Sorry, you cannot register to vote overseas because you were not registered to vote when you lived in the UK"
    ) extends ExitTemplate("exit/tooOldWhenLeft")
  }
}
