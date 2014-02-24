package uk.gov.gds.ier.transaction.overseas.parentName

import com.google.inject.Inject
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionKeys
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.OverseaStep
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.model.{InprogressOverseas, PreviousName, OverseasName}
import controllers.step.overseas.routes.ParentNameController
import controllers.step.overseas.routes.DateLeftUkController
import controllers.step.overseas.LastUkAddressController

class ParentNameStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val encryptionKeys : EncryptionKeys)
  extends OverseaStep
  with ParentNameForms
  with ParentNameMustache {

  val validation = parentNameForm

  val routes = Routes(
    get = ParentNameController.get,
    post = ParentNameController.post,
    editGet = ParentNameController.editGet,
    editPost = ParentNameController.editPost
  )
  val previousRoute = Some(DateLeftUkController.get)

  def nextStep(currentState: InprogressOverseas) = {
    LastUkAddressController.lastUkAddressStep
  }
  
  override def postSuccess(currentState: InprogressOverseas):InprogressOverseas = {
    currentState.overseasParentName match {
      case Some(OverseasName(optParentName, Some(parentPreviousName)))
        if (!parentPreviousName.hasPreviousName) =>
          currentState.copy(
            overseasParentName = Some(OverseasName(optParentName, Some(PreviousName(false, None))))
          )
      case _ => currentState
    }
//    if (currentState.parentPreviousName.isDefined && !currentState.parentPreviousName.get.hasPreviousName) {
//      currentState.copy(parentPreviousName = Some(ParentPreviousName(false, None)))
//    }
//    else currentState
  }
  
  def template(
      form:InProgressForm[InprogressOverseas],
      call:Call,
      backUrl: Option[Call]): Html = {
    parentNameMustache(form.form, call, backUrl.map(_.url))
  }
}
