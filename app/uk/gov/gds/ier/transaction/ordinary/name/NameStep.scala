package uk.gov.gds.ier.transaction.name

import controllers.step.ordinary.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressOrdinary
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.OrdinaryStep

class NameStep @Inject ()(val serialiser: JsonSerialiser,
                                val config: Config,
                                val encryptionService : EncryptionService,
                                val encryptionKeys : EncryptionKeys)
  extends OrdinaryStep
  with NameForms {

  val validation = nameForm
  val editPostRoute = NameController.editPost
  val stepPostRoute = NameController.post

  def template(form:InProgressForm[InprogressOrdinary], call:Call): Html = {
    views.html.steps.name(form, call)
  }
  def goToNext(currentState: InprogressOrdinary): SimpleResult = {
    Redirect(NinoController.get)
  }
}

