package eu.europa.esig.dss.evidencerecord.xml.validation;

import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.EvidenceRecordWrapper;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDigestMatcher;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignatureScope;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.DigestMatcherType;
import eu.europa.esig.dss.enumerations.SignatureScopeType;
import eu.europa.esig.dss.evidencerecord.common.validation.AbstractEvidenceRecordTestValidation;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DigestDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.utils.Utils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XmlEvidenceRecordDataObjectGroupSameDigestValidationTest extends AbstractEvidenceRecordTestValidation {

    @Override
    protected DSSDocument getSignedDocument() {
        return new FileDocument("src/test/resources/er-data-group-same-digest.xml");
    }

    @Override
    protected List<DSSDocument> getDetachedContents() {
        return Collections.singletonList(new DigestDocument(DigestAlgorithm.SHA512, "NhX4DJ0pPtdAJof5SyLVjlKbjMeRb4+sf933+9WvTPd309eVp6AKFr9+fz+5Vh7puq5IDan+ehh2nnGIawPzFQ=="));
    }

    @Override
    protected void checkEvidenceRecords(DiagnosticData diagnosticData) {
        super.checkEvidenceRecords(diagnosticData);

        List<EvidenceRecordWrapper> evidenceRecords = diagnosticData.getEvidenceRecords();
        assertEquals(1, evidenceRecords.size());

        EvidenceRecordWrapper evidenceRecordWrapper = evidenceRecords.get(0);
        List<XmlDigestMatcher> digestMatchers = evidenceRecordWrapper.getDigestMatchers();
        assertEquals(3, digestMatchers.size());

        List<DSSDocument> detachedContents = getDetachedContents();
        DSSDocument detachedDocument = detachedContents.get(0);

        for (XmlDigestMatcher digestMatcher : digestMatchers) {
            assertEquals(DigestMatcherType.EVIDENCE_RECORD_ARCHIVE_OBJECT, digestMatcher.getType());
            assertEquals(DigestAlgorithm.SHA512, digestMatcher.getDigestMethod());
            assertEquals(detachedDocument.getDigest(DigestAlgorithm.SHA512), Utils.toBase64(digestMatcher.getDigestValue()));
            assertTrue(digestMatcher.isDataFound());
            assertTrue(digestMatcher.isDataIntact());
        }

        List<XmlSignatureScope> evidenceRecordScopes = evidenceRecordWrapper.getEvidenceRecordScopes();
        assertEquals(1, evidenceRecordScopes.size());

        XmlSignatureScope evidenceRecordScope = evidenceRecordScopes.get(0);
        assertEquals(SignatureScopeType.FULL, evidenceRecordScope.getScope());

        assertNotNull(evidenceRecordScope.getSignerData());
        assertNotNull(evidenceRecordScope.getSignerData().getDigestAlgoAndValue());
        assertEquals(DigestAlgorithm.SHA512, evidenceRecordScope.getSignerData().getDigestAlgoAndValue().getDigestMethod());
        assertEquals(detachedDocument.getDigest(DigestAlgorithm.SHA512), Utils.toBase64(evidenceRecordScope.getSignerData().getDigestAlgoAndValue().getDigestValue()));
    }

}