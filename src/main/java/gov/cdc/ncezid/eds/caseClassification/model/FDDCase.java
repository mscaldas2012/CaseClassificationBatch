package gov.cdc.ncezid.eds.caseClassification.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FDDCase extends TransmissionHeader {
    public FDDCase(String state, String patId, String dtSpec) {
        super.setState(state);
        super.setPatId(patId);
        super.setDtSpec(dtSpec);
    }

    public String toString() {
        return "FDDCase->"+super.toString();
    }
}
