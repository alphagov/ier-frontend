package uk.gov.gds.ier.transaction.ordinary.name

import controllers.step.ordinary.routes._
import controllers.step.routes._
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
  val previousRoute = Some(DateOfBirthController.get)

  def template(form:InProgressForm[InprogressOrdinary], call:Call, backUrl: Option[Call]): Html = {
    views.html.steps.name(form, call, backUrl.map(_.url))
  }
  def goToNext(currentState: InprogressOrdinary): SimpleResult = {
    Redirect(NinoController.get)
  }
}