package uk.gov.gds.ier.template

import play.api.templates.Html
import views.html.layouts.{stepsBodyEnd, head}

object MainStepTemplate {
  def apply(content:Html, title: String, header:Html = head(), scripts:Html = stepsBodyEnd()) = {
    views.html.layouts.main (title = Some(title),stylesheets = header, scripts = scripts)(content)
  }
}
