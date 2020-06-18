package eu.europa.esig.dss.asic.xades.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import eu.europa.esig.dss.detailedreport.DetailedReport;
import eu.europa.esig.dss.detailedreport.jaxb.XmlBasicBuildingBlocks;
import eu.europa.esig.dss.detailedreport.jaxb.XmlConstraint;
import eu.europa.esig.dss.detailedreport.jaxb.XmlFC;
import eu.europa.esig.dss.detailedreport.jaxb.XmlStatus;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDigestMatcher;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.SignedDocumentValidator;

public class ASiCEWithXAdESNoSignedFileTest extends AbstractASiCWithXAdESTestValidation {

	@Override
	protected DSSDocument getSignedDocument() {
		return new FileDocument("src/test/resources/validation/no-signed-file.asice");
	}
	
	@Override
	protected void verifySimpleReport(SimpleReport simpleReport) {
		super.verifySimpleReport(simpleReport);
		
		assertEquals(Indication.TOTAL_FAILED, simpleReport.getIndication(simpleReport.getFirstSignatureId()));
		assertEquals(SubIndication.FORMAT_FAILURE, simpleReport.getSubIndication(simpleReport.getFirstSignatureId()));
	}
	
	@Override
	protected void verifyDetailedReport(DetailedReport detailedReport) {
		super.verifyDetailedReport(detailedReport);
		
		XmlBasicBuildingBlocks bbb = detailedReport.getBasicBuildingBlockById(detailedReport.getFirstSignatureId());
		assertNotNull(bbb);
		
		XmlFC fc = bbb.getFC();
		assertNotNull(fc);
		assertEquals(Indication.FAILED, fc.getConclusion().getIndication());
		assertEquals(SubIndication.FORMAT_FAILURE, fc.getConclusion().getSubIndication());
		
		for (XmlConstraint xmlConstraint : fc.getConstraint()) {
			if (MessageTag.BBB_FC_ISFP_ASICE.getId().equals(xmlConstraint.getName().getNameId())) {
				assertEquals(XmlStatus.NOT_OK, xmlConstraint.getStatus());
				assertEquals(MessageTag.BBB_FC_ISFP_ASICE_ANS.getId(), xmlConstraint.getError().getNameId());
			} else {
				assertEquals(XmlStatus.OK, xmlConstraint.getStatus());
			}
		}
	}
	
	@Override
	protected void checkBLevelValid(DiagnosticData diagnosticData) {
		
		List<SignatureWrapper> signatures = diagnosticData.getSignatures();
		assertEquals(2, signatures.size());
		
		for (SignatureWrapper signature : signatures) {
			boolean signsSignatureFile = false;
			for (XmlDigestMatcher digestMatcher : signature.getDigestMatchers()) {
				if ("signatures0".equals(digestMatcher.getName())) {
					assertFalse(digestMatcher.isDataFound());
					assertFalse(digestMatcher.isDataIntact());
					signsSignatureFile = true;
				} else {
					assertTrue(digestMatcher.isDataFound());
					assertTrue(digestMatcher.isDataIntact());
				}
			}
			
			if (signsSignatureFile) {
				assertFalse(signature.isSignatureIntact());
				assertFalse(signature.isSignatureValid());
				assertFalse(signature.isBLevelTechnicallyValid());
			} else {
				assertTrue(signature.isSignatureIntact());
				assertTrue(signature.isSignatureValid());
				assertTrue(signature.isBLevelTechnicallyValid());
			}
		}
		
	}
	
	@Override
	protected void checkContainerInfo(DiagnosticData diagnosticData) {
		assertTrue(Utils.isCollectionEmpty(diagnosticData.getContainerInfo().getContentFiles()));
	}
	
	@Override
	protected void verifyOriginalDocuments(SignedDocumentValidator validator, DiagnosticData diagnosticData) {
		for (String signatureId : diagnosticData.getSignatureIdList()) {
			List<DSSDocument> retrievedOriginalDocuments = validator.getOriginalDocuments(signatureId);
			assertTrue(Utils.isCollectionEmpty(retrievedOriginalDocuments));
		}
	}

}
