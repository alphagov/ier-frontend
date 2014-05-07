package uk.gov.gds.ier.serialiser

import uk.gov.gds.common.json.JsonSerializer

class JsonSerialiser extends JsonSerializer {
  mapper.registerModule(new JodaParseModule)
}

