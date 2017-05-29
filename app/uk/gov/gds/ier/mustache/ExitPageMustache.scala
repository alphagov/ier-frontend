package uk.gov.gds.ier.mustache

import uk.gov.gds.ier.guice.{WithRemoteAssets, WithConfig}
import uk.gov.gds.ier.langs.Messages

trait ExitPageMustache extends InheritedGovukMustache {
  self: WithRemoteAssets
    with WithConfig =>

  object ExitPages {

    case class BritishIslands() (
        implicit override val lang: Lang
    ) extends ArticleMustachio("exit/britishIslands")

    case class DontKnow() (
      implicit override val lang: Lang
      ) extends ArticleMustachio("exit/dontKnow")

    case class DontKnowScotland() (
      implicit override val lang: Lang
      ) extends ArticleMustachio("exit/dontKnowScotland")

    case class NoFranchise() (
      implicit override val lang: Lang
    ) extends ArticleMustachio("exit/noFranchise")

    case class NorthernIreland() (
        implicit override val lang: Lang
    ) extends ArticleMustachio("exit/northernIreland")

    case class Nioverseas() (
      implicit override val lang: Lang
      ) extends ArticleMustachio("exit/niOverseas")

    case class Scotland() (
      implicit override val lang: Lang
    ) extends ArticleMustachio("exit/scotland")

    case class TooYoung () (
        implicit override val lang: Lang
    ) extends ArticleMustachio("exit/tooYoung")

    case class TooYoungScotland () (
      implicit override val lang: Lang
    ) extends ArticleMustachio("exit/tooYoungScotland")

    case class TooYoungNotScotland14 () (
      implicit override val lang: Lang
      ) extends ArticleMustachio("exit/tooYoungNotScotland14")

    case class TooYoungNotScotland15 () (
      implicit override val lang: Lang
      ) extends ArticleMustachio("exit/tooYoungNotScotland15")

    case class Under16 () (
      implicit override val lang: Lang
      ) extends ArticleMustachio("exit/under16")

    case class Under18 () (
        implicit override val lang: Lang
    ) extends ArticleMustachio("exit/under18")

    case class LeftService () extends ArticleMustachio("exit/leftService")

    case class LeftUk () extends ArticleMustachio("exit/leftUk")

    case class TooOldWhenLeft () extends ArticleMustachio("exit/tooOldWhenLeft")
  }
}
