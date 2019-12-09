package gov.cdc.ncezid.eds.caseClassification;

import gov.cdc.ncezid.eds.caseClassification.model.FDDCase;
import gov.cdc.ncezid.eds.caseClassification.model.TransmissionHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class TransmissionProcessor implements ItemProcessor<TransmissionHeader, FDDCase> {

    private static final Logger log = LoggerFactory.getLogger(TransmissionProcessor.class);

    @Override
    public FDDCase process(TransmissionHeader transmission) {
        //Get all transmission for this transmission header:

        //create case with the top Transmission.
        FDDCase fddCase = new FDDCase();
        fddCase.setState(transmission.getState());
        fddCase.setPatId(transmission.getPatId());
        fddCase.setDtSpec(transmission.getDtSpec());

        //calculate case specific variables:


        log.info("Converting case... "+ fddCase + " from " + transmission);
        //persist case eventually...
        return fddCase;
    }
}
