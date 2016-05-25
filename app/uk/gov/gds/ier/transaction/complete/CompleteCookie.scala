package uk.gov.gds.ier.transaction.complete

import uk.gov.gds.ier.service.apiservice.EroAuthorityDetails

case class CompleteCookie(
    refNum: String = "",
    authority: Option[EroAuthorityDetails] = None,
    hasOtherAddress: Boolean = false,
    backToStartUrl: String = "",
    showEmailConfirmation: Boolean = false,
    showBirthdayBunting: Boolean = false,
    showDeadlineText: Boolean = false,
    gssCode: Option[String] = None,
    showYoungScot: Boolean = false,
    showTemplateCurrent: Boolean = false,
    showTemplate1: Boolean = false,
    showTemplate2: Boolean = false,
    showTemplate3: Boolean = false,
    showTemplate4: Boolean = false,
    showEnglish: Boolean = false,
    showWelsh: Boolean = false,
    splitRef1:String = "",
    splitRef2:String = ""
)
