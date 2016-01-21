package uk.gov.gds.ier.transaction.overseas.confirmation

import uk.gov.gds.ier.langs.Language
import uk.gov.gds.ier.service.{WithAddressService, AddressService}
import uk.gov.gds.ier.transaction.overseas.{OverseasControllers, WithOverseasControllers}
import uk.gov.gds.ier.controller.routes.ErrorController
import uk.gov.gds.ier.transaction.complete.routes.CompleteStep
import uk.gov.gds.ier.model.{PostalVoteOption, WaysToVoteType, ApplicationType}
import uk.gov.gds.ier.step.ConfirmationStepController
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.mustache.ErrorPageMustache
import uk.gov.gds.ier.service.apiservice.IerApiService
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.guice.WithRemoteAssets
import uk.gov.gds.ier.session.ResultHandling
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.transaction.complete.CompleteCookie

@Singleton
class ConfirmationStep @Inject() (
    val encryptionService: EncryptionService,
    val config: Config,
    val serialiser: JsonSerialiser,
    ierApi: IerApiService,
    val remoteAssets: RemoteAssets,
    val overseas: OverseasControllers,
    val addressService: AddressService
  ) extends ConfirmationStepController[InprogressOverseas]
  with WithOverseasControllers
  with ConfirmationForms
  with ErrorPageMustache
  with WithAddressService
  with ConfirmationMustache
  with ResultHandling
  with WithRemoteAssets {

  def factoryOfT() = InprogressOverseas()
  def timeoutPage() = ErrorController.ordinaryTimeout

  val routing: Routes = Routes(
    get = routes.ConfirmationStep.get,
    post = routes.ConfirmationStep.post,
    editGet = routes.ConfirmationStep.get,
    editPost = routes.ConfirmationStep.post
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
            request.getToken.map(_.timeTaken)
          )

          logSession()

          val isTemplateCurrent = validApplication.postalOrProxyVote.exists { postalVote =>
            postalVote.deliveryMethod.equals(None)
          } || validApplication.postalOrProxyVote.equals(None)

          val isTemplate1 = validApplication.postalOrProxyVote.exists { postalVote =>
            val isPostalVoteOptionSelected = postalVote.postalVoteOption.exists(_ == true)

            val isEmailOrPost = postalVote.deliveryMethod.exists{
              deliveryMethod => deliveryMethod.isEmail && deliveryMethod.emailAddress.exists(_.nonEmpty)
            }

            val isPostalOrProxyVote = validApplication.postalOrProxyVote.exists { postalVote =>
              (postalVote.typeVote == WaysToVoteType.ByPost)
            }

            isPostalVoteOptionSelected && isEmailOrPost && isPostalOrProxyVote
          }

          val isTemplate2 = validApplication.postalOrProxyVote.exists { postalVote =>

            val isEmailOrPost = postalVote.deliveryMethod.exists{
              deliveryMethod => deliveryMethod.isEmail && deliveryMethod.emailAddress.exists(_.nonEmpty)
            }

            val isPostalOrProxyVote = validApplication.postalOrProxyVote.exists { postalVote =>
              (postalVote.typeVote == WaysToVoteType.ByProxy)
            }

            isEmailOrPost && isPostalOrProxyVote
          }

          val isTemplate3 = validApplication.postalOrProxyVote.exists { postalVote =>
            val isPostalVoteOptionSelected = postalVote.postalVoteOption.exists(_ == true)

            val isEmailOrPost = postalVote.deliveryMethod.exists{
              deliveryMethod => deliveryMethod.isPost
            }
            val isPostalOrProxyVote = validApplication.postalOrProxyVote.exists { postalVote =>
              (postalVote.typeVote == WaysToVoteType.ByPost)
            }
            isPostalVoteOptionSelected && isEmailOrPost && isPostalOrProxyVote
          }

          val isTemplate4 = validApplication.postalOrProxyVote.exists { postalVote =>

            val isEmailOrPost = postalVote.deliveryMethod.exists{
              deliveryMethod => deliveryMethod.isPost
            }

            val isPostalOrProxyVote = validApplication.postalOrProxyVote.exists { postalVote =>
              (postalVote.typeVote == WaysToVoteType.ByProxy)
            }

            isEmailOrPost && isPostalOrProxyVote
          }

          val isEnglish = Language.emailLang.equals("en")

          val isWelsh = Language.emailLang.equals("cy")

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


          var gssCode = None: Option[String]
          if (validApplication.parentsAddress.isDefined) {gssCode = (validApplication.parentsAddress.get.gssCode)}
          if (validApplication.lastUkAddress.isDefined) {gssCode = (validApplication.lastUkAddress.get.gssCode)}



          if (gssCode.isEmpty)
          {
            if (validApplication.overseasParentName.isEmpty) {
              gssCode = addressService.lookupGssCode(validApplication.lastUkAddress.get.postcode)
            }

            else {gssCode = addressService.lookupGssCode(validApplication.parentsAddress.get.postcode) }
          }

          val completeStepData = CompleteCookie(
            refNum = refNum,
            authority = Some(response.localAuthority),
            backToStartUrl = config.ordinaryStartUrl,
            showEmailConfirmation = (isPostalOrProxyVoteEmailPresent | isContactEmailPresent),
            showBirthdayBunting =  isBirthdayToday,
            gssCode = gssCode,
            showTemplateCurrent = isTemplateCurrent,
            showTemplate1 = isTemplate1,
            showTemplate2 = isTemplate2,
            showTemplate3 = isTemplate3,
            showTemplate4 = isTemplate4,
            showEnglish = isEnglish,
            showWelsh = isWelsh
          )

          Redirect(CompleteStep.complete())
            .emptySession()
            .storeCompleteCookie(completeStepData)
        }
      )
  }
}
