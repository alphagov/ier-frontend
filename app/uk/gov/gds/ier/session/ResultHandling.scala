package uk.gov.gds.ier.session

import play.api.mvc.Cookie
import uk.gov.gds.ier.guice.WithConfig

trait ResultHandling {
  self: WithConfig =>

  private[session] def createSecureCookie ( name : String, value : String) : Cookie = {
    Cookie (name, value, None, "/", None, config.cookiesSecured, true)
  }
}