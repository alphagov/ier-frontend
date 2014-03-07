package uk.gov.gds.ier.transaction.forces.rank

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.{Rank, InprogressForces}
import scala.Some
import controllers.step.forces.routes._

class RankMustacheTest
  extends FlatSpec
  with Matchers
  with RankForms
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val rankMustache = new RankMustache {}

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = rankForm
    val rankModel = rankMustache.transformFormStepToMustacheData(
      emptyApplicationForm,
      RankController.post,
      Some(ContactAddressController.get))

    rankModel.question.title should be("What is your service number?")
    rankModel.question.postUrl should be("/register-to-vote/forces/rank")
    rankModel.question.backUrl should be("/register-to-vote/forces/contact-address")

    rankModel.serviceNumber.value should be("")
    rankModel.rank.value should be("")

  }

  it should "progress form with filled applicant name should produce Mustache Model with name values present" in {
    val partiallyFilledApplicationForm = rankForm.fill(InprogressForces(
      rank = Some(Rank(
        serviceNumber = Some("123456"),
        rank = Some("Captain")
      ))
    ))

    val rankModel = rankMustache.transformFormStepToMustacheData(
      partiallyFilledApplicationForm,
      RankController.post,
      Some(ContactAddressController.get))

    rankModel.question.title should be("What is your service number?")
    rankModel.question.postUrl should be("/register-to-vote/forces/rank")
    rankModel.question.backUrl should be("/register-to-vote/forces/contact-address")

    rankModel.serviceNumber.value should be("123456")
    rankModel.rank.value should be("Captain")

  }
}
