package eu.europa.esig.dss.evidencerecord.common.validation;

import eu.europa.esig.dss.spi.x509.tsp.TimestampToken;

import java.util.List;

/**
 * Represents an Evidence Record's ArchiveTimeStamp object.
 * Contains the hash tree as well as the time-stamp binaries.
 */
public class ArchiveTimeStampObject implements EvidenceRecordObject {

    private static final long serialVersionUID = 5881635666028328980L;

    /** The ordered list of data object groups containing their digest values */
    private List<? extends DigestValueGroup> hashTree;

    /** Time-stamp token */
    private TimestampToken timestampToken;

    /** Contains validation information for the timestampToken */
    private List<CryptographicInformation> cryptographicInformationList;

    /**
     * Default constructor
     */
    public ArchiveTimeStampObject() {
        // empty
    }

    /**
     * Gets the ordered hash tree
     *
     * @return a list of {@link DigestValueGroup}s
     */
    public List<? extends DigestValueGroup> getHashTree() {
        return hashTree;
    }

    /**
     * Sets the ordered hash tree
     *
     * @param hashTree a list of {@link DigestValueGroup}s
     */
    public void setHashTree(List<? extends DigestValueGroup> hashTree) {
        this.hashTree = hashTree;
    }

    /**
     * Gets the time-stamp
     *
     * @return {@link TimestampToken}
     */
    public TimestampToken getTimestampToken() {
        return timestampToken;
    }

    /**
     * Sets the time-stamp token
     *
     * @param timestampToken {@link TimestampToken}
     */
    public void setTimestampToken(TimestampToken timestampToken) {
        this.timestampToken = timestampToken;
    }

    /**
     * Gets cryptographic information list
     *
     * @return a list of {@link CryptographicInformation}s
     */
    public List<CryptographicInformation> getCryptographicInformationList() {
        return cryptographicInformationList;
    }

    /**
     * Sets cryptographic information list
     *
     * @param cryptographicInformationList a list of {@link CryptographicInformation}s
     */
    public void setCryptographicInformationList(List<CryptographicInformation> cryptographicInformationList) {
        this.cryptographicInformationList = cryptographicInformationList;
    }

}