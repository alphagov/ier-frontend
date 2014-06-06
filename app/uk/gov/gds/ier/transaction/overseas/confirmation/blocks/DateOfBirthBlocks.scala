package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import controllers.step.overseas.routes

trait DateOfBirthBlocks {
  self: ConfirmationBlock =>

  def dateOfBirth = {
    val dob = form.dateOfBirth map { dob =>
      dob.toString("d MMMM yyyy")
    } getOrElse ""

    ConfirmationQuestion(
      title = "Date of birth",
      editLink = routes.DateOfBirthController.editGet.url,
      changeName = "date of birth",
      content = ifComplete(keys.dob) {
        List(dob)
      }
    )
  }
}
