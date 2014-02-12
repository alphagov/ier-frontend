package controllers.step.overseas

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.{InprogressOverseas, DateLeftSpecial, LastRegisteredType}

object DateLeftArmyController extends StubController[InprogressOverseas] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/overseas/date-left-army"
  
  def dateLeftArmyStep = confirmationIf { application =>
    application.dateLeftSpecial.exists {
      _.registeredType == LastRegisteredType.Army
    }
  }
}

object DateLeftCrownController extends StubController[InprogressOverseas] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/overseas/date-left-crown"
  
  def dateLeftCrownStep = confirmationIf { application =>
    application.dateLeftSpecial.exists {
      _.registeredType == LastRegisteredType.Crown
    }
  }
}

object DateLeftCouncilController extends StubController[InprogressOverseas] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/overseas/date-left-council"
  
  def dateLeftCouncilStep = confirmationIf { application =>
    application.dateLeftSpecial.exists {
      _.registeredType == LastRegisteredType.Council
    }
  }
}
