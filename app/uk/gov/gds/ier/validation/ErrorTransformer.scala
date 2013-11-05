package uk.gov.gds.ier.validation

import uk.gov.gds.ier.model.KeyForError
import play.api.data.Form

class ErrorTransformer {

  def transform[T](errorForm: Form[T]):Form[T] = {
    val transformedErrors = errorForm.errors.map(
      error => {
        val parseKeysForError = error.args.foldLeft(Seq.empty[KeyForError]){
          (sequence, arg) => if (arg.isInstanceOf[KeyForError]) {
            sequence :+ arg.asInstanceOf[KeyForError]
          } else {
            sequence
          }
        }

        if (error.args.isEmpty || parseKeysForError.isEmpty) {
          error
        } else {
          error.copy(key = parseKeysForError(0).key)
        }
      }
    )
    errorForm.copy(errors = transformedErrors)
  }
}
