package uk.gov.gds.ier.step.country

import controllers._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.validation.constraints.CountryConstraints
import uk.gov.gds.ier.controller.StepController
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.{Country, InprogressApplication}
import play.api.templates.Html
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import org.jba.Mustache
import views.html.layouts.{stepsBodyEnd, head}

class CountryController @Inject ()(val serialiser: JsonSerialiser,
                                   val config:Config,
                                   val encryptionService : EncryptionService,
                                   val encryptionKeys : EncryptionKeys,
                                   val countryTransformer : CountryMustacheTransformer)
  extends StepController
  with CountryConstraints
  with WithSerialiser
  with WithConfig
  with WithEncryption
  with CountryForms {

  val validation = countryForm
  val editPostRoute = step.routes.CountryController.editPost
  val stepPostRoute = step.routes.CountryController.post

  def template(form:InProgressForm, call:Call): Html = {
    val data = countryTransformer.transformFormStepToMustacheData(form,call.url).getOrElse(None)
    val content:Html = Mustache.render("ordinary/country", data)
    views.html.layouts.main (title = Some("Register to Vote - Where do you live?"),stylesheets = head(), scripts = stepsBodyEnd())(content)
  }

  def goToNext(currentState: InprogressApplication): SimpleResult = {
    currentState.country match {
      case Some(Country("Northern Ireland")) => Redirect(routes.ExitController.northernIreland)
      case Some(Country("Scotland")) => Redirect(routes.ExitController.scotland)
      case _ => Redirect(step.routes.NationalityController.get)
    }
  }
}

