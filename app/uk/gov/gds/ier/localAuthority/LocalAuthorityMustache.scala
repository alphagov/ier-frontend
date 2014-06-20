package uk.gov.gds.ier.localAuthority

import uk.gov.gds.ier.mustache.InheritedGovukMustache
import uk.gov.gds.ier.guice.WithRemoteAssets
import uk.gov.gds.ier.guice.WithConfig
import uk.gov.gds.ier.model.LocalAuthority
import uk.gov.gds.ier.mustache.MustacheModel
import uk.gov.gds.ier.validation.ErrorTransformForm

trait LocalAuthorityMustache
  extends InheritedGovukMustache with MustacheModel {
  self: WithRemoteAssets
  with WithConfig =>

    case class LocalAuthorityPage (
        localAuthority: LocalAuthority,
        override val sourcePath: String
    ) (
        implicit override val lang: Lang
    ) extends ArticleMustachio("localAuthority/show")

    object LocalAuthorityPage {
      def apply(
        localAuthority: LocalAuthority,
        sourcePath: Option[String]
      ): LocalAuthorityPage = {
        LocalAuthorityPage(localAuthority, sourcePath getOrElse "")
      }
    }
    case class LocalAuthorityLookupPage (
        postcode: Field,
        override val sourcePath: String,
        postUrl: String
    ) (
        implicit override val lang: Lang
    ) extends ArticleMustachio("localAuthority/lookup")

    object LocalAuthorityLookupPage {
      def apply(
          implicit form: ErrorTransformForm[LocalAuthorityRequest],
          sourcePath: Option[String],
          postUrl: String
      ): LocalAuthorityLookupPage = {
        LocalAuthorityLookupPage(
          TextField(key = keys.postcode),
          sourcePath getOrElse "",
          postUrl
        )
      }
    }
}
