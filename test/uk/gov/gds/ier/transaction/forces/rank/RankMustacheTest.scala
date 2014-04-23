package uk.gov.gds.ier.transaction.forces.rank

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.{Rank}
import scala.Some
import controllers.step.forces.routes._
import uk.gov.gds.ier.transaction.forces.InprogressForces

class RankMustacheTest
  extends FlatSpec
  with Matchers
  with RankForms
  with ErrorMessages
  with FormKeys
  with TestHelpers
  with RankMustache {


  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = rankForm

    val rankModel = mustache.data(
      emptyApplicationForm,
      Call("POST", "/register-to-vote/forces/rank"),
      InprogressForces()
    ).data.asInstanceOf[RankModel]

    rankModel.question.title should be("What is your service number?")
    rankModel.question.postUrl should be("/register-to-vote/forces/rank")

    rankModel.serviceNumber.value should be("")
    rankModel.rank.value should be("")

  }

  it should "progress form with filled applicant name should produce Mustache Model with name values present" in {
    val partiallyFilledApplication = InprogressForces(
      rank = Some(Rank(
        serviceNumber = Some("123456"),
        rank = Some("Captain")
      ))
    )

    val partiallyFilledApplicationForm = rankForm.fill(partiallyFilledApplication)

    val rankModel =  mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/forces/rank"),
      partiallyFilledApplication
    ).data.asInstanceOf[RankModel]

    rankModel.question.title should be("What is your service number?")
    rankModel.question.postUrl should be("/register-to-vote/forces/rank")

    rankModel.serviceNumber.value should be("123456")
    rankModel.rank.value should be("Captain")
  }
}
