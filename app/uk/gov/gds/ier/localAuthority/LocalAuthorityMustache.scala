package uk.gov.gds.ier.localAuthority

import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.guice.WithRemoteAssets
import uk.gov.gds.ier.guice.WithConfig
import uk.gov.gds.ier.model.LocalAuthority

trait LocalAuthorityMustache
  extends StepMustache {

  self: WithRemoteAssets
  with WithConfig =>
    case class LocalAuthorityPage (
        localAuthority: LocalAuthority,
        pageTitle: String = "Contact your local authority"
    ) extends ArticlePage("localAuthority/show")

}