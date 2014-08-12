package uk.gov.gds.ier.transaction.crown.job

import uk.gov.gds.ier.test.FormTestSuite

class JobFormTests
  extends FormTestSuite
  with JobForms {

  it should "error out on empty json" in {
    val js = JsNull
    jobForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(3)
        hasErrors.errorMessages("job.jobTitle") should be(Seq("Please answer this question"))
        hasErrors.errorMessages("job.govDepartment") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "describe all missing fields" in {
    val js = Json.toJson(
      Map(
        "job.jobTitle" -> "",
        "job.govDepartment" -> ""
      )
    )
    jobForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(3)
        hasErrors.errorMessages("job.jobTitle") should be(Seq("Please answer this question"))
        hasErrors.errorMessages("job.govDepartment") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on a missing field" in {
    val js = Json.toJson(
      Map(
        "job.jobTitle" -> "Doctor"
      )
    )
    jobForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("job.govDepartment") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }


  it should "successfully bind" in {
    val js = Json.toJson(
      Map(
        "job.jobTitle" -> "Doctor",
        "job.govDepartment" -> "Fake department"
      )
    )
    jobForm.bind(js).fold(
      hasErrors => {
        fail(serialiser.toJson(hasErrors.prettyPrint))
      },
      success => {
        success.job.isDefined should be(true)
        val job = success.job.get
        job.jobTitle should be(Some("Doctor"))
        job.govDepartment should be(Some("Fake department"))

      }
    )
  }
}

