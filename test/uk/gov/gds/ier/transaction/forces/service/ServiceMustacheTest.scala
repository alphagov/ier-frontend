package uk.gov.gds.ier.transaction.forces.service

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.{ServiceType, Service}
import scala.Some
import controllers.step.forces.routes._
import uk.gov.gds.ier.transaction.forces.InprogressForces


class ServiceMustacheTest
  extends FlatSpec
  with Matchers
  with ServiceForms
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serviceMustache = new ServiceMustache {}

  it should "empty progress form should produce empty Model" in {
    val emptyApplication = InprogressForces()
    val emptyApplicationForm = serviceForm
    
    val serviceModel = serviceMustache.transformFormStepToMustacheData(
      emptyApplication, emptyApplicationForm,
      ServiceController.post, Some(NinoController.get))

    serviceModel.question.title should be("Which of the services are you in?")
    serviceModel.question.postUrl should be("/register-to-vote/forces/service")
    serviceModel.question.backUrl should be("/register-to-vote/forces/nino")

    serviceModel.serviceFieldSet.classes should be("")
    serviceModel.royalNavy.attributes should be("")
    serviceModel.britishArmy.attributes should be("")
    serviceModel.royalAirForce.attributes should be("")
    serviceModel.regiment.value should be("")
    serviceModel.regimentShowFlag.value should be("")
  }

  it should "fully filled applicant statement should produce Mustache Model with statement values present" in {

    val filledApp = InprogressForces(
      service = Some(Service(
        serviceName = Some(ServiceType.BritishArmy),
        regiment = Some("my regiment")
      ))
    )

    val filledForm = serviceForm.fillAndValidate(filledApp)

    val serviceModel = serviceMustache.transformFormStepToMustacheData(
      filledApp, filledForm, ServiceController.post, Some(NinoController.get))

    serviceModel.question.title should be("Which of the services are you in?")
    serviceModel.question.postUrl should be("/register-to-vote/forces/service")
    serviceModel.question.backUrl should be("/register-to-vote/forces/nino")

    serviceModel.serviceFieldSet.classes should be("")
    serviceModel.royalNavy.attributes should be("")
    serviceModel.britishArmy.attributes should be("checked=\"checked\"")
    serviceModel.royalAirForce.attributes should be("")
    serviceModel.regiment.value should be("my regiment")
    serviceModel.regimentShowFlag.value should be("-open")
  }
}
