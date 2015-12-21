package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

trait NameBlocks {
  self: ConfirmationBlock =>

  def name = {
    val nameStr = List(
      form(keys.name.firstName).value,
      form(keys.name.middleNames).value,
      form(keys.name.lastName).value
    ).flatten.mkString(" ")

    ConfirmationQuestion(
      title = "Full name",
      editLink = overseas.NameStep.routing.editGet.url,
      changeName = "full name",
      content = ifComplete(keys.overseasName.name) {
        List(nameStr)
      }
    )
  }

  def previousName = {
    val hasPreviousName = form(keys.previousName.hasPreviousName).value
    val changedNameBeforeLeavingUKOption = form(keys.previousName.changedNameBeforeLeavingUKOption).value
    val nameChangeReason = form(keys.previousName.reason).value match {
      case Some(reason) if reason.nonEmpty => List("Reason for the name change:", reason)
      case _ => List.empty
    }
    val prevNameContent =  hasPreviousName match {
      case Some("true") => {
        if (changedNameBeforeLeavingUKOption.getOrElse("").equalsIgnoreCase("true")) {
          if (form(keys.previousName.previousName).value.getOrElse("").equals(None)) {
            //This OS scenario is >> NO && YES(empty)
            List("Prefer not to say")
          } else if (form(keys.previousName.previousName.firstName).value.getOrElse("").equals("") &
            form(keys.previousName.previousName.lastName).value.getOrElse("").equals("")) {
            List("Prefer not to say")
          } else {
            //This OS scenario is >> YES && NO(completed)
            List(
              List(
                form(keys.previousName.previousName.firstName).value,
                form(keys.previousName.previousName.middleNames).value,
                form(keys.previousName.previousName.lastName).value
              ).flatten.mkString(" ")
            ) ++ nameChangeReason
          }
        } else {
          //This OS scenario is >> YES & N/A
          List(
            List(
              form(keys.previousName.previousName.firstName).value,
              form(keys.previousName.previousName.middleNames).value,
              form(keys.previousName.previousName.lastName).value
            ).flatten.mkString(" ")
          ) ++ nameChangeReason
        }
      }
      case Some("false") => {
          if(changedNameBeforeLeavingUKOption.getOrElse("").equalsIgnoreCase("other")) {
            //This OS scenario is >> NO && OTHER
            List("Prefer not to say")
          } else {
            //This OS scenario is >> NO && NO
            List("I have not changed my name")
          }
      }
      case _ => List("I have not changed my name")
    }
    ConfirmationQuestion(
      title = "Previous name",
      editLink = overseas.NameStep.routing.editGet.url,
      changeName = "previous name",
      content = ifComplete(keys.overseasName.previousName) {
        prevNameContent
      }
    )
  }
}
