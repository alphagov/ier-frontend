package controllers.step.crown

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.controller.{DeclarationPdfDownloadController => GuicedController}

object DeclarationPdfDownloadController extends DelegatingController[GuicedController]{
  def download = delegate.download
}
