/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 *
 * This file is part of the "DSS - Digital Signature Services" project.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.validation.policy;

import java.util.Date;

import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.XmlDom;
import eu.europa.esig.dss.validation.CertificateWrapper;
import eu.europa.esig.dss.validation.SignatureWrapper;
import eu.europa.esig.dss.validation.TokenProxy;
import eu.europa.esig.dss.validation.policy.rules.ExceptionMessage;
import eu.europa.esig.dss.validation.process.POEExtraction;
import eu.europa.esig.dss.validation.report.DiagnosticData;

/**
 * This class stores the references to data exchanged and manipulated by different sub validation processes.
 */
public class ProcessParameters {

	/**
	 * This variable contains the diagnostic data which is used to carry out all validation processes. It is extracted
	 * from the signature(s) being validated. This data is independent of the form of source signature (PDF, XAdES,
	 * PAdES, ASiC).
	 */
	protected DiagnosticData diagnosticData;

	/**
	 * This is the policy data to be used by the validation process. This data are not mandatory but in this case the
	 * ValidationContextInitialisation sub process will fail.
	 */
	protected ValidationPolicy validationPolicy;

	/**
	 * This is the countersignature policy data to be used by the validation process. This data are not mandatory but in this case the
	 * ValidationContextInitialisation sub process will fail.
	 */
	private ValidationPolicy countersignatureValidationPolicy;

	/**
	 * This is the current validation policy (either signature or countersignature).
	 */
	protected ValidationPolicy currentValidationPolicy;

	/**
	 * This is the current time against which the validation process is carried out.
	 */
	protected Date currentTime;

	/**
	 * This variable contains the Signing Certificate Node from diagnostic data. It is initialised by
	 * IdentificationOfTheSignersCertificate sub process.
	 * This variable is different for each context.
	 */
	private CertificateWrapper signingCertificate;

	/**
	 * Represents the current main signature DOM element being validated. This element provides general information used
	 * in validation process like the list of used certificates.
	 */
	protected SignatureWrapper signatureContext;

	/**
	 * Represents the current signature DOM element being validated:<br>
	 * in the case of main signature validation {@code contextElement} is the signature element being validated;<br>
	 * in case of Timestamp signature validation {@code contextElement} is the timestamp element being validated.
	 */
	protected TokenProxy contextElement;

	/**
	 * This {@code XmlDom} is returned by the Basic Building Blocks process (see BasicBuildingBlocks) and
	 * it depicts the validation detailed report.
	 */
	private XmlDom basicBuildingBlocksReport;

	/**
	 * This {@code XmlDom} is returned by the Basic Validation process (see BasicValidation) and
	 * it depicts the validation detailed report.
	 */
	private XmlDom bvData;

	/**
	 * This {@code XmlDom} is returned by the Basic Timestamp Validation process (see TimestampValidation)
	 * and it depicts the validation detailed report.
	 */
	private XmlDom tsData;

	/**
	 * This {@code XmlDom} is returned by the AdEST Validation process (see AdESTValidation) and
	 * it depicts the validation detailed report.
	 */
	private XmlDom adestData;

	/**
	 * This {@code XmlDom} is returned by the Long Term Validation process (see LongTermValidation) and
	 * it depicts the validation detailed report.
	 */
	private XmlDom ltvData;

	private POEExtraction poe;

	/**
	 * See {@link #diagnosticData}
	 *
	 * @return
	 */
	public DiagnosticData getDiagnosticData() {
		return diagnosticData;
	}

	/**
	 * See {@link #diagnosticData}
	 *
	 * @return
	 */
	public void setDiagnosticData(final DiagnosticData diagnosticData) {
		this.diagnosticData = diagnosticData;
	}

	/**
	 * See {@link #validationPolicy}
	 *
	 * @return
	 */
	public ValidationPolicy getValidationPolicy() {
		return validationPolicy;
	}

	/**
	 * See {@link #validationPolicy}
	 *
	 * @return
	 */
	public void setValidationPolicy(final ValidationPolicy validationPolicy) {
		this.validationPolicy = validationPolicy;
	}

	public void setCountersignatureValidationPolicy(final ValidationPolicy countersignatureValidationPolicy) {
		this.countersignatureValidationPolicy = countersignatureValidationPolicy;
	}

	public ValidationPolicy getCountersignatureValidationPolicy() {
		return countersignatureValidationPolicy;
	}

	/**
	 * See {@link #currentValidationPolicy}
	 *
	 * @return
	 */
	public ValidationPolicy getCurrentValidationPolicy() {
		return currentValidationPolicy;
	}

	/**
	 * See {@link #currentValidationPolicy}
	 *
	 * @return
	 */
	public void setCurrentValidationPolicy(final ValidationPolicy currentValidationPolicy) {
		this.currentValidationPolicy = currentValidationPolicy;
	}

	/**
	 * See {@link #signingCertificate}
	 *
	 * @return
	 */
	public CertificateWrapper getSigningCertificate() {
		return signingCertificate;
	}

	/**
	 * See {@link #signingCertificate}
	 *
	 * @return
	 */
	public void setSigningCertificate(final CertificateWrapper signingCertificate) {
		this.signingCertificate = signingCertificate;
	}

	/**
	 * See {@link #basicBuildingBlocksReport}
	 *
	 * @return
	 */
	public XmlDom getBasicBuildingBlocksReport() {
		return basicBuildingBlocksReport;
	}

	/**
	 * See {@link #basicBuildingBlocksReport}
	 *
	 * @return
	 */
	public void setBBBData(final XmlDom bbbData) {
		this.basicBuildingBlocksReport = bbbData;
	}

	/**
	 * See {@link #bvData}
	 *
	 * @return
	 */
	public XmlDom getBvData() {
		return bvData;
	}

	/**
	 * See {@link #bvData}
	 *
	 * @return
	 */
	public void setBvData(XmlDom bvData) {
		this.bvData = bvData;
	}

	/**
	 * See {@link #tsData}
	 *
	 * @return
	 */
	public XmlDom getTsData() {
		return tsData;
	}

	/**
	 * See {@link #tsData}
	 *
	 * @return
	 */
	public void setTsData(XmlDom tsData) {
		this.tsData = tsData;
	}

	/**
	 * See {@link #adestData}
	 *
	 * @return
	 */

	public XmlDom getAdestData() {
		return adestData;
	}

	/**
	 * See {@link #adestData}
	 *
	 * @return
	 */
	public void setAdestData(XmlDom adestData) {
		this.adestData = adestData;
	}

	/**
	 * See {@link #ltvData}
	 *
	 * @return
	 */

	public XmlDom getLtvData() {
		return ltvData;
	}

	/**
	 * See {@link #ltvData}
	 *
	 * @return
	 */
	public void setLtvData(XmlDom ltvData) {
		this.ltvData = ltvData;
	}

	/**
	 * See {@link #currentTime}
	 *
	 * @return
	 */
	public Date getCurrentTime() {
		return currentTime;
	}

	/**
	 * See {@link #currentTime}
	 *
	 * @return
	 */
	public void setCurrentTime(final Date currentTime) {
		if (this.currentTime != null) {
			throw new DSSException(ExceptionMessage.EXCEPTION_CTVSBIOO);
		}
		this.currentTime = currentTime;
	}

	/**
	 * See {@link #signatureContext}
	 *
	 * @return
	 */
	public SignatureWrapper getSignatureContext() {
		return signatureContext;
	}

	/**
	 * See {@link #signatureContext}
	 *
	 * @param signature
	 */
	public void setSignatureContext(final SignatureWrapper signature) {
		this.signatureContext = signature;
	}

	/**
	 * See {@link #contextElement}
	 *
	 * @return
	 */
	public TokenProxy getContextElement() {
		return contextElement;
	}

	/**
	 * See {@link #contextElement}
	 *
	 * @param contextElement
	 */
	public void setContextElement(final TokenProxy contextElement) {
		this.contextElement = contextElement;
	}

	public POEExtraction getPOE() {
		return poe;
	}

	public void setPOE(final POEExtraction poe) {
		this.poe = poe;
	}

}
