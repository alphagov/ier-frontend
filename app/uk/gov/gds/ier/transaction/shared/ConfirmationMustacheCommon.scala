package uk.gov.gds.ier.transaction.shared


  case class EitherErrorOrContent(blockContent: Option[List[String]], blockError: Option[String])
  object BlockContent {
    def apply(values: List[String]) = EitherErrorOrContent(blockContent = Some(values), blockError = None)
    def apply(value: String) = EitherErrorOrContent(blockContent = Some(List(value)), blockError = None)
  }
  object BlockError {
    def apply(value: String) = EitherErrorOrContent(blockContent = None, blockError = Some(value))
  }

