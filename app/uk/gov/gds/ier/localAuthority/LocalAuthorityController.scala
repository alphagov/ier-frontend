package uk.gov.gds.ier.localAuthority

import com.google.inject.Inject
import play.api.mvc.Controller
import play.api.mvc.Action
import uk.gov.gds.ier.client.ApiResults
import uk.gov.gds.ier.serialiser.{ JsonSerialiser, WithSerialiser }
import uk.gov.gds.ier.validation.IerForms
import uk.gov.gds.ier.session.RequestHandling
import uk.gov.gds.ier.guice.WithEncryption
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.service.apiservice.ConcreteIerApiService
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.validation.FormKeys
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.guice.WithRemoteAssets
import uk.gov.gds.ier.guice.WithConfig
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.assets.RemoteAssets
import play.api.mvc.Request
import play.api.mvc.AnyContent
import com.google.inject.Singleton

@Singleton
class LocalAuthorityController @Inject() (
    val ierApiService: ConcreteIerApiService,
    val addressService: AddressService,
    val serialiser: JsonSerialiser,
    val encryptionService: EncryptionService,
    val config: Config,
    val remoteAssets: RemoteAssets
) extends Controller
  with ApiResults
  with WithSerialiser
  with FormKeys
  with RequestHandling
  with WithEncryption
  with Logging
  with LocalAuthorityLookupForm
  with IerForms
  with LocalAuthorityMustache
  with WithRemoteAssets
  with WithConfig {

  def ero(gssCode: String, sourcePath: Option[String]) = Action { implicit request =>
     logger.info(s"FIRST SOURCEPATH : $sourcePath -------------")
    var sPath : String = ""

    if(sourcePath.isDefined) {
      sPath = toCleanFormat(sourcePath.toString())
    }
    logger.info(s"SECOND SOURCEPATH : $sPath -------------")

    val localAuthorityContactDetails = ierApiService.getLocalAuthorityByGssCode(gssCode).contactDetails
    Ok(LocalAuthorityShowPage(localAuthorityContactDetails, Some(sPath)))
  }

  def doLookup(sourcePath: Option[String]) = Action { implicit request =>

    var sPath : String = ""
    if(sourcePath.isDefined) {
      sPath = toCleanFormat(sourcePath.toString())
    }


    localAuthorityLookupForm.bindFromRequest().fold(
      hasErrors => BadRequest(LocalAuthorityPostcodePage(
        hasErrors,
        Some(sPath),
        routes.LocalAuthorityController.showLookup(Some(sPath)).url
      )),
      success => {
        val gssCode = addressService.lookupGssCode(success.postcode)
        gssCode match {
          case Some(gss) => Redirect(
            routes.LocalAuthorityController.ero(gss, Some(sPath))
          )
          case None => BadRequest(
            LocalAuthorityPostcodePage(
              localAuthorityLookupForm.fill(success).withError(
                keys.postcode.key,
                "lookup_error_noneFound"
              ),
              Some(sPath),
              routes.LocalAuthorityController.showLookup(Some(sPath)).url
            )
          )
        }
      }
    )
  }

  def showLookup(sourcePath: Option[String]) = Action { implicit request =>

    var sPath : String = ""
    if(sourcePath.isDefined) {
      sPath = toCleanFormat(sourcePath.toString())
    }


    getGssCode(Some(sPath), request) match {
      case Some(gssCode) =>
        Redirect(
          routes.LocalAuthorityController.ero(gssCode, Some(sPath))
        )
      case None => Ok(LocalAuthorityPostcodePage(
        localAuthorityLookupForm,
        Some(sPath),
        routes.LocalAuthorityController.showLookup(Some(sPath)).url
        ))
    }
  }

  def getGssCode(sourcePath: Option[String], request: Request[AnyContent]): Option[String] = {
    var sPath : String = ""

    if(sourcePath.isDefined) {
      sPath = toCleanFormat(sourcePath.toString())
    }


    Some(sPath) match {
        case Some(srcPath) if (srcPath.contains("overseas"))  =>
          request.getApplication[InprogressOverseas] flatMap (_.lastUkAddress flatMap (_.gssCode))
        case Some(srcPath) if (srcPath.contains("crown")) =>
          request.getApplication[InprogressCrown] flatMap
            (_.address flatMap (_.address flatMap (_.gssCode)))
        case Some(srcPath) if (srcPath.contains("forces"))  =>
          request.getApplication[InprogressForces] flatMap
            (_.address flatMap (_.address flatMap (_.gssCode)))
        case _ =>
          request.getApplication[InprogressOrdinary] flatMap (_.address flatMap (_.gssCode))
      }
  }

  def cleanFormat(sPath:String) = {
    sPath.replaceAll("[<> '']", "")

  }

  def toCleanFormat(sPath: String) = {
    cleanFormat(sPath)
  }
}
