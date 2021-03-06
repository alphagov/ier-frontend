package uk.gov.gds.ier.transaction.crown.confirmation

import uk.gov.gds.ier.langs.Language
import uk.gov.gds.ier.step.ConfirmationStepController
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.service.apiservice.IerApiService
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.guice.WithRemoteAssets
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.transaction.crown.{InprogressCrown, CrownControllers, WithCrownControllers}
import uk.gov.gds.ier.model.{PostalVoteOption, WaysToVoteType, ApplicationType}
import uk.gov.gds.ier.transaction.complete.CompleteCookie
import uk.gov.gds.ier.session.ResultHandling
import uk.gov.gds.ier.transaction.complete.routes.CompleteStep
import uk.gov.gds.ier.controller.routes.ErrorController

@Singleton
class ConfirmationStep @Inject() (
    val encryptionService: EncryptionService,
    val config: Config,
    val serialiser: JsonSerialiser,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    ierApi: IerApiService,
    val crown: CrownControllers
) extends ConfirmationStepController[InprogressCrown]
  with ConfirmationForms
  with ConfirmationMustache
  with ResultHandling
  with WithCrownControllers
  with WithRemoteAssets {

  def factoryOfT() = InprogressCrown()
  def timeoutPage() = ErrorController.crownTimeout

  val routing: Routes = Routes(
    get = routes.ConfirmationStep.get,
    post = routes.ConfirmationStep.post,
    editGet = routes.ConfirmationStep.get,
    editPost = routes.ConfirmationStep.post
  )

  val validation = confirmationForm

  def get = ValidSession in Action {
    implicit request =>
      val filledForm = validation.fillAndValidate(application)
      val html = mustache(filledForm, routing.post, application).html
      Ok(html).refreshToken
  }

  def post = ValidSession in Action {
    implicit request =>
      validation.fillAndValidate(application).fold(
        hasErrors => {
          Ok(mustache(hasErrors, routing.post, application).html).refreshToken
        },
        validApplication => {
          val refNum = ierApi.generateCrownReferenceNumber(validApplication)
          val remoteClientIP = request.headers.get("X-Real-IP")
          val len:Int = refNum.length();
          val splitRef1:String = refNum.substring(0, len/2);
          val splitRef2:String = refNum.substring(len/2,len );

          val response = ierApi.submitCrownApplication(
            remoteClientIP,
            validApplication,
            Some(refNum),
            request.getToken.map(_.timeTaken)
          )

          logSession()

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

          val isTemplateCurrent = validApplication.postalOrProxyVote.exists { postalVote =>
            postalVote.deliveryMethod.equals(None)
          } || validApplication.postalOrProxyVote.equals(None)

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

          val isBirthdayToday = validApplication.dob.exists(_.dob.exists(_.isToday))


          //Get GSSCode of application if it has used addr lookup
          var gssCode =(validApplication.address.get.address.get.gssCode)

          if  (gssCode == None)
          {
            //if it has been a manual addr entry, pull back postcode and lookup appropriate gsscode
            gssCode = (addressService.lookupGssCode(validApplication.address.get.address.get.postcode))
          }

          val completeStepData = CompleteCookie(
            refNum = refNum,
            authority = Some(response.localAuthority),
            backToStartUrl = config.ordinaryStartUrl,
            showEmailConfirmation = (isPostalOrProxyVoteEmailPresent | isContactEmailPresent),
            showBirthdayBunting =  isBirthdayToday,
            showDeadlineText = true,
            gssCode = gssCode,
            showTemplateCurrent = isTemplateCurrent,
            showTemplate1 = isTemplate1,
            showTemplate2 = isTemplate2,
            showTemplate3 = isTemplate3,
            showTemplate4 = isTemplate4,
            showEnglish = isEnglish,
            showWelsh = isWelsh,
            splitRef1 = splitRef1,
            splitRef2 = splitRef2
          )

          Redirect(CompleteStep.complete())
            .emptySession()
            .storeCompleteCookie(completeStepData)
        }
      )
  }
}
