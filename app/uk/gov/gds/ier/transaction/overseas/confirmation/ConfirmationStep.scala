package uk.gov.gds.ier.transaction.overseas.confirmation

import uk.gov.gds.ier.model.{WaysToVoteType, ApplicationType}
import uk.gov.gds.ier.step.ConfirmationStepController
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import controllers.step.overseas.routes.ConfirmationController
import controllers.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.mustache.ErrorPageMustache
import uk.gov.gds.ier.service.apiservice.IerApiService
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.guice.WithRemoteAssets
import uk.gov.gds.ier.session.ResultHandling
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.transaction.complete.CompleteCookie

class ConfirmationStep @Inject() (
    val encryptionService: EncryptionService,
    val config: Config,
    val serialiser: JsonSerialiser,
    ierApi: IerApiService,
    val remoteAssets: RemoteAssets
  ) extends ConfirmationStepController[InprogressOverseas]
  with ConfirmationForms
  with ErrorPageMustache
  with ConfirmationMustache
  with ResultHandling
  with WithRemoteAssets {

  def factoryOfT() = InprogressOverseas()
  def timeoutPage() = ErrorController.ordinaryTimeout

  val routing: Routes = Routes(
    get = ConfirmationController.get,
    post = ConfirmationController.post,
    editGet = ConfirmationController.get,
    editPost = ConfirmationController.post
  )

  val validation = confirmationForm

  def get = ValidSession in Action {
    implicit request =>
      application.identifyApplication match {
        case ApplicationType.DontKnow => NotFound(ErrorPage.NotFound(request.path))
        case _ => {
          val filledForm = validation.fillAndValidate(application)
          val html = mustache(filledForm, routing.post, application).html
          Ok(html).refreshToken
        }
      }
  }

  def post = ValidSession in Action {
    implicit request =>
      validation.fillAndValidate(application).fold(
        hasErrors => {
          Ok(mustache(hasErrors, routing.post, application).html).refreshToken
        },
        validApplication => {
          val refNum = ierApi.generateOverseasReferenceNumber(validApplication)
          val remoteClientIP = request.headers.get("X-Real-IP")

          val response = ierApi.submitOverseasApplication(
            remoteClientIP,
            validApplication,
            Some(refNum),
            request.getToken.map(_.timeTaken),
            sessionId = request.getToken.flatMap(_.refreshToken.id)
          )

          logSession()

          val isPostalOrProxyVoteEmailPresent = validApplication.postalOrProxyVote.exists { postalVote =>
            (postalVote.typeVote != WaysToVoteType.InPerson) &
              postalVote.postalVoteOption.exists(_ == true) & postalVote.deliveryMethod.exists{ deliveryMethod =>
              deliveryMethod.deliveryMethod.exists(_ == "email") && deliveryMethod.emailAddress.exists(_.nonEmpty)
            }
          }

          val isContactEmailPresent = validApplication.contact.exists(
            _.email.exists{ emailContact =>
              emailContact.contactMe & emailContact.detail.exists(_.nonEmpty)
            }
          )

          val isBirthdayToday = validApplication.dob.exists(_.isToday)

          val completeStepData = CompleteCookie(
            refNum = refNum,
            authority = Some(response.localAuthority),
            backToStartUrl = config.ordinaryStartUrl,
            showEmailConfirmation = (isPostalOrProxyVoteEmailPresent | isContactEmailPresent),
            showBirthdayBunting =  isBirthdayToday
          )

          Redirect(CompleteController.complete())
            .emptySession()
            .storeCompleteCookie(completeStepData)
        }
      )
  }
}
