package eu.europa.esig.dss.asic.cades.validation.evidencerecord;

import eu.europa.esig.dss.enumerations.DigestMatcherType;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.model.ReferenceValidation;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.simplereport.jaxb.XmlEvidenceRecord;
import eu.europa.esig.dss.spi.x509.tsp.TimestampToken;
import eu.europa.esig.dss.spi.x509.tsp.TimestampedReference;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.evidencerecord.EvidenceRecord;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ASiCEWithCAdESLevelLTWithEvidenceRecordMultiFilesDifferentAlgoValidationTest extends AbstractASiCEWithCAdESWithEvidenceRecordTestValidation {

    @Override
    protected DSSDocument getSignedDocument() {
        return new FileDocument("src/test/resources/validation/evidencerecord/cades-lt-with-er-multi-files-different-algo.sce");
    }

    @Override
    protected int getNumberOfExpectedEvidenceScopes() {
        return 4; // signature file + 3 signed data files covered
    }

    @Override
    protected boolean allArchiveDataObjectsProvidedToValidation() {
        return false;
    }

    @Override
    protected void checkDetachedEvidenceRecords(List<EvidenceRecord> detachedEvidenceRecords) {
        assertEquals(1, Utils.collectionSize(detachedEvidenceRecords));

        EvidenceRecord evidenceRecord = detachedEvidenceRecords.get(0);

        int archiveObjectCounter = 0;
        int orphanReferencesCounter = 0;
        List<ReferenceValidation> referenceValidationList = evidenceRecord.getReferenceValidation();
        for (ReferenceValidation referenceValidation : referenceValidationList) {
            if (DigestMatcherType.EVIDENCE_RECORD_ARCHIVE_OBJECT.equals(referenceValidation.getType())) {
                assertTrue(referenceValidation.isFound());
                assertFalse(referenceValidation.isIntact());
                ++archiveObjectCounter;
            } else if (DigestMatcherType.EVIDENCE_RECORD_ORPHAN_REFERENCE.equals(referenceValidation.getType())) {
                assertFalse(referenceValidation.isFound());
                assertFalse(referenceValidation.isIntact());
                ++orphanReferencesCounter;
            }
        }
        assertEquals(getNumberOfExpectedEvidenceScopes(), archiveObjectCounter);
        assertEquals(1, orphanReferencesCounter);

        List<TimestampedReference> timestampedReferences = evidenceRecord.getTimestampedReferences();
        assertTrue(Utils.isCollectionNotEmpty(timestampedReferences));

        int tstCounter = 0;
        List<TimestampToken> timestamps = evidenceRecord.getTimestamps();
        for (TimestampToken timestampToken : timestamps) {
            assertTrue(timestampToken.isProcessed());
            assertTrue(timestampToken.isMessageImprintDataFound());
            assertTrue(timestampToken.isMessageImprintDataIntact());
            ++tstCounter;
        }
        assertEquals(1, tstCounter);
    }

    @Override
    protected void verifySimpleReport(SimpleReport simpleReport) {
        List<XmlEvidenceRecord> signatureEvidenceRecords = simpleReport.getSignatureEvidenceRecords(simpleReport.getFirstSignatureId());
        assertEquals(1, signatureEvidenceRecords.size());
        assertEquals(Indication.FAILED, signatureEvidenceRecords.get(0).getIndication());
        assertEquals(SubIndication.HASH_FAILURE, signatureEvidenceRecords.get(0).getSubIndication());
    }

}
