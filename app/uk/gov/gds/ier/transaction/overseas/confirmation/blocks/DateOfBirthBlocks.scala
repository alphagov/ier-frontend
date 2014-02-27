package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import controllers.step.overseas.routes

trait DateOfBirthBlocks {
  self: ConfirmationBlock =>

  def dateOfBirth = {
    val dob = form.dateOfBirth getOrElse ""

    ConfirmationQuestion(
      title = "What is your date of birth?",
      editLink = routes.DateOfBirthController.editGet.url,
      changeName = "date of birth",
      content = ifComplete(keys.dob) {
        s"<p>$dob</p>"
      }
    )
  }
}
