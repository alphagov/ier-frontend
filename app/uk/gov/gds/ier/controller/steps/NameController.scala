package uk.gov.gds.ier.controller.steps

import uk.gov.gds.ier.controller.StepController
import uk.gov.gds.ier.validation.{InProgressForm, ErrorTransformer, WithErrorTransformer}
import uk.gov.gds.ier.serialiser.{JsonSerialiser, WithSerialiser}
import com.google.inject.Inject
import uk.gov.gds.ier.model.InprogressApplication
import play.api.mvc.{Call, SimpleResult}
import play.api.templates.Html
import play.api.data.Form

//@Singleton
class NameController @Inject ()(val serialiser: JsonSerialiser,
                                val errorTransformer: ErrorTransformer) extends StepController
                                                                        with WithSerialiser
                                                                        with WithErrorTransformer {

  val validation: Form[InprogressApplication] = ???
  val template: (InProgressForm, Call) => Html = ???
  val editPostRoute: Call = ???
  val stepPostRoute: Call = ???

  def goToNext(currentState: InprogressApplication): SimpleResult = ???
}
