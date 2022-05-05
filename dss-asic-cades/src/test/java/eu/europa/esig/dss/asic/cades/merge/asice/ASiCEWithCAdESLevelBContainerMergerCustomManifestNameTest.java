package eu.europa.esig.dss.asic.cades.merge.asice;

import eu.europa.esig.dss.asic.cades.ASiCWithCAdESSignatureParameters;
import eu.europa.esig.dss.asic.cades.SimpleASiCWithCAdESFilenameFactory;
import eu.europa.esig.dss.asic.cades.merge.ASiCEWithCAdESContainerMerger;
import eu.europa.esig.dss.asic.cades.merge.AbstractWithCAdESTestMerge;
import eu.europa.esig.dss.asic.cades.signature.ASiCWithCAdESService;
import eu.europa.esig.dss.asic.common.merge.ASiCContainerMerger;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.jaxb.XmlManifestFile;
import eu.europa.esig.dss.enumerations.ASiCContainerType;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.MimeType;
import org.junit.jupiter.api.BeforeEach;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ASiCEWithCAdESLevelBContainerMergerCustomManifestNameTest extends AbstractWithCAdESTestMerge {

    private DSSDocument documentToSignOne;
    private DSSDocument documentToSignTwo;

    private ASiCWithCAdESService service;

    private ASiCWithCAdESSignatureParameters firstSignatureParameters;
    private ASiCWithCAdESSignatureParameters secondSignatureParameters;

    @BeforeEach
    public void init() {
        documentToSignOne = new InMemoryDocument("Hello World!".getBytes(), "hello.txt", MimeType.TEXT);
        documentToSignTwo = new InMemoryDocument("Bye World!".getBytes(), "bye.txt", MimeType.TEXT);

        service = new ASiCWithCAdESService(getCompleteCertificateVerifier());

        firstSignatureParameters = new ASiCWithCAdESSignatureParameters();
        firstSignatureParameters.setSignatureLevel(SignatureLevel.CAdES_BASELINE_B);
        firstSignatureParameters.aSiC().setContainerType(ASiCContainerType.ASiC_E);
        firstSignatureParameters.bLevel().setSigningDate(new Date());

        secondSignatureParameters = new ASiCWithCAdESSignatureParameters();
        secondSignatureParameters.setSignatureLevel(SignatureLevel.CAdES_BASELINE_B);
        secondSignatureParameters.aSiC().setContainerType(ASiCContainerType.ASiC_E);
        secondSignatureParameters.bLevel().setSigningDate(new Date());
    }

    @Override
    protected DSSDocument getFirstSignedContainer() {
        SimpleASiCWithCAdESFilenameFactory filenameFactory = new SimpleASiCWithCAdESFilenameFactory();
        filenameFactory.setSignatureFilename("signature001.p7s");
        filenameFactory.setManifestFilename("ASiCManifestAAA.xml");
        getService().setAsicFilenameFactory(filenameFactory);
        return super.getFirstSignedContainer();
    }

    @Override
    protected DSSDocument getSecondSignedContainer() {
        SimpleASiCWithCAdESFilenameFactory filenameFactory = new SimpleASiCWithCAdESFilenameFactory();
        filenameFactory.setSignatureFilename("signature002.p7s");
        filenameFactory.setManifestFilename("ASiCManifestAAA.xml");
        getService().setAsicFilenameFactory(filenameFactory);
        return super.getSecondSignedContainer();
    }

    @Override
    protected ASiCContainerMerger getASiCContainerMerger(DSSDocument containerOne, DSSDocument containerTwo) {
        ASiCContainerMerger containerMerger = super.getASiCContainerMerger(containerOne, containerTwo);
        assertTrue(containerMerger instanceof ASiCEWithCAdESContainerMerger);
        ASiCEWithCAdESContainerMerger cadesContainerMerger = (ASiCEWithCAdESContainerMerger) containerMerger;

        SimpleASiCWithCAdESFilenameFactory filenameFactory = new SimpleASiCWithCAdESFilenameFactory();
        filenameFactory.setManifestFilename("ASiCManifestBBB.xml");
        cadesContainerMerger.setAsicFilenameFactory(filenameFactory);

        return cadesContainerMerger;
    }

    @Override
    protected void checkContainerInfo(DiagnosticData diagnosticData) {
        super.checkContainerInfo(diagnosticData);

        assertEquals(2, diagnosticData.getContainerInfo().getManifestFiles().size());

        boolean aaaManifestFound = false;
        boolean bbbManifestFound = false;
        for (XmlManifestFile manifestFile : diagnosticData.getContainerInfo().getManifestFiles()) {
            if ("META-INF/ASiCManifestAAA.xml".equals(manifestFile.getFilename())) {
                aaaManifestFound = true;
            } else if ("META-INF/ASiCManifestBBB.xml".equals(manifestFile.getFilename())) {
                bbbManifestFound = true;
            }
        }
        assertTrue(aaaManifestFound);
        assertTrue(bbbManifestFound);
    }

    @Override
    protected List<DSSDocument> getFirstSignedData() {
        return Collections.singletonList(documentToSignOne);
    }

    @Override
    protected List<DSSDocument> getSecondSignedData() {
        return Collections.singletonList(documentToSignTwo);
    }

    @Override
    protected ASiCWithCAdESSignatureParameters getFirstSignatureParameters() {
        firstSignatureParameters.setSigningCertificate(getSigningCert());
        firstSignatureParameters.setCertificateChain(getCertificateChain());
        return firstSignatureParameters;
    }

    @Override
    protected ASiCWithCAdESSignatureParameters getSecondSignatureParameters() {
        secondSignatureParameters.setSigningCertificate(getSigningCert());
        secondSignatureParameters.setCertificateChain(getCertificateChain());
        return secondSignatureParameters;
    }

    @Override
    protected String getFirstSigningAlias() {
        return GOOD_USER;
    }

    @Override
    protected String getSecondSigningAlias() {
        return RSA_SHA3_USER;
    }

    @Override
    protected ASiCWithCAdESService getService() {
        return service;
    }

    @Override
    protected ASiCContainerType getExpectedASiCContainerType() {
        return ASiCContainerType.ASiC_E;
    }

}