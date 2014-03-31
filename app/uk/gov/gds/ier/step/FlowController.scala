package uk.gov.gds.ier.step

trait FlowController[T] {
  type ApplicationStep = (T, Step[T])
  type FlowControl = ApplicationStep => ApplicationStep

  object AlwaysGoToNextStep {
    def apply(): FlowControl = {
      case (currentState, step) => {
        (currentState, GoTo(step.nextStep(currentState).routes.get))
      }
    }
  }

  object SkipStepIfComplete {

    def apply(): FlowControl = {
      case (currentState, step) => {
        (currentState, getNextStep(currentState, step))
      }
    }

    def getNextStep(app:T, step:Step[T]):Step[T] = {
      if (step.isStepComplete(app)) {
        getNextStep(app, step.nextStep(app))
      } else {
        GoTo(step.routes.get)
      }
    }
  }

  object TransformApplication {

    def apply(transform:T=>T): FlowControl = {
      case (currentState, step) => {
        (transform(currentState), step)
      }
    }
  }

  object BranchOn {
    def apply[A](extractA:T => A)(branchOn:A=>FlowControl): FlowControl = {
      case (currentState, step) => {
        val a = extractA(currentState)
        val flowControl = branchOn(a)
        flowControl(currentState, step)
      }
    }
  }
}
