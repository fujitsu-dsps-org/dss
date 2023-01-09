package eu.europa.esig.dss.ws.signature.soap.client;

import eu.europa.esig.dss.ws.dto.RemoteDocument;
import eu.europa.esig.dss.ws.dto.ToBeSignedDTO;
import eu.europa.esig.dss.ws.signature.dto.DataToSignExternalCmsDTO;
import eu.europa.esig.dss.ws.signature.dto.SignMessageDigestExternalCmsDTO;

import java.io.Serializable;

import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

/**
 * This SOAP interface provides a possibility of CMS signature creation suitable for PAdES signing
 *
 */
@WebService(targetNamespace = "http://signature.dss.esig.europa.eu/")
public interface SoapExternalCMSService extends Serializable {

    /**
     * Returns a DTBS (Data To Be Signed) for Signature Value creation.
     *
     * @param dataToSign
     *            {@link DataToSignExternalCmsDTO} containing message-digest computed on PDF's signature ByteRange
     *            and a set of signature driving parameters
     * @return {@link ToBeSignedDTO} data to be signed representation
     */
    @WebResult(name = "response")
    ToBeSignedDTO getDataToSign(@WebParam(name = "dataToSign") DataToSignExternalCmsDTO dataToSign);

    /**
     * Creates a CMS signature signing the provided {@code messageDigest} compliant for PAdES signature enveloping.
     *
     * @param signMessageDigest
     *            {@link SignMessageDigestExternalCmsDTO} containing message-digest computed on PDF's signature ByteRange,
     *            set of signature driving parameters and a signatureValue computed on DTBS
     * @return {@link RemoteDocument} representing a CMS signature suitable for PAdES-BASELINE creation
     */
    @WebResult(name = "response")
    RemoteDocument signMessageDigest(@WebParam(name = "signMessageDigest") SignMessageDigestExternalCmsDTO signMessageDigest);

}
