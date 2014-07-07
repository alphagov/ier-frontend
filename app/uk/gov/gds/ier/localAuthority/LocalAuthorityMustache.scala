package uk.gov.gds.ier.localAuthority

import uk.gov.gds.ier.mustache.InheritedGovukMustache
import uk.gov.gds.ier.guice.WithRemoteAssets
import uk.gov.gds.ier.guice.WithConfig
import uk.gov.gds.ier.model.LocalAuthority
import uk.gov.gds.ier.mustache.MustacheModel
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.LocalAuthorityContactDetails

trait LocalAuthorityMustache
  extends InheritedGovukMustache with MustacheModel {
  self: WithRemoteAssets
  with WithConfig =>

    case class LocalAuthorityShowPage (
        localAuthorityContact: Option[LocalAuthorityContactDetails],
        override val sourcePath: String
    ) (
        implicit override val lang: Lang
    ) extends ArticleMustachio("localAuthority/show")

    object LocalAuthorityShowPage {
      def apply(
        localAuthorityContact: Option[LocalAuthorityContactDetails],
        sourcePath: Option[String]
      ): LocalAuthorityShowPage = {
        LocalAuthorityShowPage(localAuthorityContact, sourcePath getOrElse "")
      }
    }

    case class LocalAuthorityPostcodePage (
        postcode: Field,
        override val sourcePath: String,
        postUrl: String
    ) (
        implicit override val lang: Lang
    ) extends ArticleMustachio("localAuthority/lookup")

    object LocalAuthorityPostcodePage {
      def apply(
          implicit form: ErrorTransformForm[LocalAuthorityRequest],
          sourcePath: Option[String],
          postUrl: String
      ): LocalAuthorityPostcodePage = {
        LocalAuthorityPostcodePage(
          TextField(key = keys.postcode),
          sourcePath getOrElse "",
          postUrl
        )
      }
    }
}
