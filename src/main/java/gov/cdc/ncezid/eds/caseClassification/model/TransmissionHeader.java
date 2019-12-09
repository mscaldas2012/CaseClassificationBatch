package gov.cdc.ncezid.eds.caseClassification.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransmissionHeader {
    private String state;
    private String patId;
    private String serotypeSummary;
    private String dtSpec;

}
