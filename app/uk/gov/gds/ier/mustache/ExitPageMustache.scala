package uk.gov.gds.ier.mustache

import uk.gov.gds.ier.guice.WithRemoteAssets

trait ExitPageMustache extends StepMustache {
  self: WithRemoteAssets =>

  object ExitPages {
    abstract class ExitTemplate(template:String) extends Mustachio(template) {

      val pageTitle:String

      override def render() = GovukTemplate(
        mainContent = super.render(),
        pageTitle = pageTitle,
        contentClasses = "exit"
      )
    }

    case class BritishIslands(
        pageTitle: String = "Register to Vote - Channel Islands and the Isle Of Man"
    ) extends ExitTemplate("exit/britishIslands")

    case class DontKnow(
        pageTitle: String = "Register to Vote - You need to find out whether you're 18 or over"
    ) extends ExitTemplate("exit/dontKnow")

    case class NoFranchise(
        pageTitle: String = "Register to Vote - Sorry, you can’t register to vote"
    ) extends ExitTemplate("exit/noFranchise")

    case class NorthernIreland(
        pageTitle: String = "Register to Vote - Voter canvass form for Northern Ireland"
    ) extends ExitTemplate("exit/northernIreland")

    case class Scotland(
        pageTitle: String = "Register to Vote - Voter registration forms for Scotland"
    ) extends ExitTemplate("exit/scotland")

    case class TooYoung (
        pageTitle: String = "Register to Vote - Sorry, you can’t register to vote until you are at least 16"
    ) extends ExitTemplate("exit/tooYoung")

    case class Under18 (
        pageTitle: String = "Register to Vote - You need to find out your date of birth"
    ) extends ExitTemplate("exit/under18")

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