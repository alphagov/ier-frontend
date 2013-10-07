package uk.gov.gds.ier.service

import com.google.inject.Singleton

@Singleton
class EroService {
  def lookupGSS(postcode:String) = {
    //Using gds_test_ero_token for now as it will resolve to the same token as the explorer for easier testing
    "F09999970"
  }
}
