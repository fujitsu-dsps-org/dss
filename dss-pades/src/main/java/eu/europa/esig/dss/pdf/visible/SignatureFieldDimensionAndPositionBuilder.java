package eu.europa.esig.dss.pdf.visible;

import eu.europa.esig.dss.enumerations.SignerTextHorizontalAlignment;
import eu.europa.esig.dss.enumerations.SignerTextVerticalAlignment;
import eu.europa.esig.dss.enumerations.VisualSignatureAlignmentHorizontal;
import eu.europa.esig.dss.enumerations.VisualSignatureAlignmentVertical;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.pades.SignatureFieldParameters;
import eu.europa.esig.dss.pades.SignatureImageParameters;
import eu.europa.esig.dss.pades.SignatureImageTextParameters;
import eu.europa.esig.dss.pdf.AnnotationBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignatureFieldDimensionAndPositionBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(SignatureFieldDimensionAndPositionBuilder.class);

    private static final String NOT_SUPPORTED_VERTICAL_ALIGNMENT_ERROR_MESSAGE = "not supported vertical alignment: ";
    private static final String NOT_SUPPORTED_HORIZONTAL_ALIGNMENT_ERROR_MESSAGE = "not supported horizontal alignment: ";

    private static final int DEFAULT_DPI = DPIUtils.getDpi(null);

    /** Visual signature parameters */
    protected final SignatureImageParameters imageParameters;

    /** The font metrics */
    private final DSSFontMetrics fontMetrics;

    /** The page's rotation value */
    private final int pageRotation;

    /** The signature field rectangle */
    private AnnotationBox pageBox;

    /** The annotation box representing a target signature field dimensions when applicable */
    private AnnotationBox signatureFieldAnnotationBox;

    /** Cached {@code SignatureFieldDimensionAndPosition} */
    private SignatureFieldDimensionAndPosition dimensionAndPosition;

    /**
     * Default constructor
     *
     * @param imageParameters {@link SignatureImageParameters}
     * @param fontMetrics {@link DSSFontMetrics}
     * @param pageBox {@link AnnotationBox} defining the page's dimensions
     * @param pageRotation page rotation value
     */
    public SignatureFieldDimensionAndPositionBuilder(final SignatureImageParameters imageParameters,
                                                     final DSSFontMetrics fontMetrics,
                                                     final AnnotationBox pageBox,
                                                     final int pageRotation) {
        this.imageParameters = imageParameters;
        this.fontMetrics = fontMetrics;
        this.pageBox = pageBox;
        this.pageRotation = pageRotation;
    }

    /**
     * This method sets the target annotation box to wrap the signature representation into
     *
     * @param signatureFieldAnnotationBox {@link AnnotationBox}
     * @return this {@link SignatureFieldDimensionAndPositionBuilder}
     */
    public SignatureFieldDimensionAndPositionBuilder setSignatureFieldAnnotationBox(AnnotationBox signatureFieldAnnotationBox) {
        this.signatureFieldAnnotationBox = signatureFieldAnnotationBox;
        return this;
    }

    /**
     * Builds the {@code SignatureFieldDimensionAndPosition}
     *
     * @return {@link SignatureFieldDimensionAndPosition}
     */
    public SignatureFieldDimensionAndPosition build() {
        if (dimensionAndPosition == null) {
            dimensionAndPosition = new SignatureFieldDimensionAndPosition();
            initDpi();
            initRotation();
            assignImageBoundaryBox();
            assignImagePosition();
            alignHorizontally();
            alignVertically();
            rotateSignatureField();
        }
        return dimensionAndPosition;
    }

    private void initDpi() {
        ImageResolution imageResolution;
        if (imageParameters.getImage() != null) {
            try {
                imageResolution = ImageUtils.secureReadMetadata(imageParameters);
            } catch (Exception e) {
                LOG.warn("Cannot access the image metadata : {}. Returns default info.", e.getMessage());
                imageResolution = new ImageResolution(imageParameters.getDpi(), imageParameters.getDpi());
            }
        } else {
            imageResolution = new ImageResolution(DEFAULT_DPI, DEFAULT_DPI);
        }
        dimensionAndPosition.setImageResolution(imageResolution);
    }

    private void initRotation() {
        int rotation = ImageRotationUtils.getRotation(imageParameters.getRotation(), pageRotation);
        dimensionAndPosition.setGlobalRotation(rotation);
        if (ImageRotationUtils.isSwapOfDimensionsRequired(rotation)) {
            pageBox = new AnnotationBox(0, 0, pageBox.getMaxY(), pageBox.getMaxX());
        }
    }

    private void assignImageBoundaryBox() {
        AnnotationBox imageBoundaryBox = getSignatureFieldBoundaryBox();
        float imageWidth = imageBoundaryBox.getWidth();
        float imageHeight = imageBoundaryBox.getHeight();

        float width = imageWidth;
        float height = imageHeight;

        SignatureFieldParameters fieldParameters = imageParameters.getFieldParameters();
        SignatureImageTextParameters textParameters = imageParameters.getTextParameters();
        // if text is present
        if (textParameters != null && !textParameters.isEmpty()) {
            if (fontMetrics == null) {
                throw new DSSException("DSSFontMetrics shall be defined!");
            }

            float padding = textParameters.getPadding();
            // native implementation uses dpi-independent font
            AnnotationBox textBox = computeTextDimension(textParameters, padding);
            float textWidth = textBox.getWidth();
            float textHeight = textBox.getHeight();

            switch (textParameters.getSignerTextPosition()) {
                case LEFT:
                    if (fieldParameters.getWidth() == 0) {
                        width += imageParameters.getImage() != null || width == 0 ? textWidth : 0;
                    } else {
                        imageWidth -= imageParameters.getImage() != null || width == 0 ? textWidth : 0;
                    }
                    if (fieldParameters.getHeight() == 0) {
                        height = Math.max(height, textHeight);
                    }
                    dimensionAndPosition.setImageBoxX(width - imageWidth);
                    textImageVerticalAlignment(height, imageHeight, textHeight);
                    break;
                case RIGHT:
                    if (fieldParameters.getWidth() == 0) {
                        width += imageParameters.getImage() != null || width == 0 ? textWidth : 0;
                    } else {
                        imageWidth -= imageParameters.getImage() != null || width == 0 ? textWidth : 0;
                    }
                    if (fieldParameters.getHeight() == 0) {
                        height = Math.max(height, textHeight);
                    }
                    dimensionAndPosition.setTextBoxX(width - textWidth);
                    textImageVerticalAlignment(height, imageHeight, textHeight);
                    break;
                case TOP:
                    if (fieldParameters.getWidth() == 0) {
                        width = Math.max(width, textWidth);
                    }
                    if (fieldParameters.getHeight() == 0) {
                        height += imageParameters.getImage() != null || height == 0 ? textHeight : 0;
                    } else {
                        imageHeight -= imageParameters.getImage() != null || height == 0 ? textHeight : 0;
                    }
                    dimensionAndPosition.setTextBoxY(height - textHeight);
                    textImageHorizontalAlignment(width, imageWidth, textWidth);
                    break;
                case BOTTOM:
                    if (fieldParameters.getWidth() == 0) {
                        width = Math.max(width, textWidth);
                    }
                    if (fieldParameters.getHeight() == 0) {
                        height += imageParameters.getImage() != null || height == 0 ? textHeight : 0;
                    } else {
                        imageHeight -= imageParameters.getImage() != null || height == 0 ? textHeight : 0;
                    }
                    dimensionAndPosition.setImageBoxY(height - imageHeight);
                    textImageHorizontalAlignment(width, imageWidth, textWidth);
                    break;
                default:
                    break;
            }

            dimensionAndPosition.setTextBoxWidth(textWidth);
            dimensionAndPosition.setTextBoxHeight(textHeight);

            dimensionAndPosition.setTextX(dimensionAndPosition.getTextBoxX() + padding);
            dimensionAndPosition.setTextY(dimensionAndPosition.getTextBoxY() + padding);
            dimensionAndPosition.setTextWidth(dimensionAndPosition.getTextBoxWidth() - 2 * padding);
            dimensionAndPosition.setTextHeight(dimensionAndPosition.getTextBoxHeight() - 2 * padding);
        }

        if (ImageRotationUtils.isSwapOfDimensionsRequired(dimensionAndPosition.getGlobalRotation())) {
            float temp = width;
            width = height;
            height = temp;
        }

        dimensionAndPosition.setImageBoxWidth(imageWidth);
        dimensionAndPosition.setImageBoxHeight(imageHeight);
        dimensionAndPosition.setBoxWidth(width);
        dimensionAndPosition.setBoxHeight(height);
    }

    /**
     * Returns the signature field boundary box based on the parameters or/and provided image
     *
     * @return {@link AnnotationBox}
     */
    private AnnotationBox getSignatureFieldBoundaryBox() {
        float width, height;
        if (signatureFieldAnnotationBox != null) {
            width = signatureFieldAnnotationBox.getWidth();
            height = signatureFieldAnnotationBox.getHeight();
            if (ImageRotationUtils.isSwapOfDimensionsRequired(dimensionAndPosition.getGlobalRotation())) {
                width = signatureFieldAnnotationBox.getHeight();
                height = signatureFieldAnnotationBox.getWidth();
            }
        } else {
            SignatureFieldParameters fieldParameters = imageParameters.getFieldParameters();
            width = fieldParameters.getWidth();
            height = fieldParameters.getHeight();
        }

        float scaleFactor = ImageUtils.getScaleFactor(imageParameters.getZoom());
        DSSDocument docImage = imageParameters.getImage();
        if (docImage != null) {
            AnnotationBox imageBoundaryBox = ImageUtils.getImageBoundaryBox(docImage);
            dimensionAndPosition.setImageWidth(imageBoundaryBox.getWidth() * scaleFactor);
            dimensionAndPosition.setImageHeight(imageBoundaryBox.getHeight() * scaleFactor);
            if (width == 0) {
                width = imageBoundaryBox.getWidth();
                width *= DPIUtils.getPageScaleFactor(dimensionAndPosition.getImageResolution().getXDpi());
            }
            if (height == 0) {
                height = imageBoundaryBox.getHeight();
                height *= DPIUtils.getPageScaleFactor(dimensionAndPosition.getImageResolution().getYDpi());
            }
        }
        width *= scaleFactor;
        height *= scaleFactor;

        return new AnnotationBox(0, 0, width, height);
    }

    private AnnotationBox computeTextDimension(SignatureImageTextParameters textParameters, float padding) {
        float properSize = textParameters.getFont().getSize()
                * ImageUtils.getScaleFactor(imageParameters.getZoom()); // scale text block

        return fontMetrics.computeTextBoundaryBox(textParameters.getText(), properSize, padding);
    }

    private void textImageVerticalAlignment(float height, float imageHeight, float textHeight) {
        SignerTextVerticalAlignment verticalAlignment = imageParameters.getTextParameters()
                .getSignerTextVerticalAlignment();
        switch (verticalAlignment) {
            case TOP:
                dimensionAndPosition.setTextBoxY(height - textHeight);
                dimensionAndPosition.setImageBoxY(height - imageHeight);
                break;
            case BOTTOM:
                dimensionAndPosition.setTextBoxY(0);
                dimensionAndPosition.setImageBoxY(0);
                break;
            case MIDDLE:
                dimensionAndPosition.setTextBoxY((height - textHeight) / 2);
                dimensionAndPosition.setImageBoxY((height - imageHeight) / 2);
                break;
            default:
                throw new IllegalStateException(NOT_SUPPORTED_VERTICAL_ALIGNMENT_ERROR_MESSAGE + verticalAlignment);
        }
    }

    private void textImageHorizontalAlignment(float width, float imageWidth, float textWidth) {
        SignerTextHorizontalAlignment horizontalAlignment = imageParameters.getTextParameters()
                .getSignerTextHorizontalAlignment();
        switch (horizontalAlignment) {
            case LEFT:
                dimensionAndPosition.setTextBoxX(0);
                dimensionAndPosition.setImageBoxX(0);
                break;
            case RIGHT:
                dimensionAndPosition.setTextBoxX(width - textWidth);
                dimensionAndPosition.setImageBoxX(width - imageWidth);
                break;
            case CENTER:
                dimensionAndPosition.setTextBoxX((width - textWidth) / 2);
                dimensionAndPosition.setImageBoxX((width - imageWidth) / 2);
                break;
            default:
                throw new IllegalStateException(NOT_SUPPORTED_HORIZONTAL_ALIGNMENT_ERROR_MESSAGE + horizontalAlignment);
        }
    }

    private void assignImagePosition() {
        if (imageParameters.getImage() != null) {
            switch (imageParameters.getImageScaling()) {
                case STRETCH:
                    dimensionAndPosition.setImageX(dimensionAndPosition.getImageBoxX());
                    dimensionAndPosition.setImageY(dimensionAndPosition.getImageBoxY());
                    dimensionAndPosition.setImageWidth(dimensionAndPosition.getImageBoxWidth());
                    dimensionAndPosition.setImageHeight(dimensionAndPosition.getImageBoxHeight());
                    break;

                case ZOOM_AND_CENTER:
                    float x, y, width, height;
                    float imageRatio = dimensionAndPosition.getImageWidth() / dimensionAndPosition.getImageHeight();
                    float boxRatio = dimensionAndPosition.getImageBoxWidth() / dimensionAndPosition.getImageBoxHeight();
                    if (imageRatio < boxRatio) {
                        width = dimensionAndPosition.getImageBoxHeight() * imageRatio;
                        height = dimensionAndPosition.getImageBoxHeight();
                        x = dimensionAndPosition.getImageBoxX() + (dimensionAndPosition.getImageBoxWidth() - width) / 2f;
                        y = dimensionAndPosition.getImageBoxY();
                    } else {
                        width = dimensionAndPosition.getImageBoxWidth();
                        height = dimensionAndPosition.getImageBoxWidth() / imageRatio;
                        x = dimensionAndPosition.getImageBoxX();
                        y = dimensionAndPosition.getImageBoxY() + (dimensionAndPosition.getImageBoxHeight() - height) / 2f;
                    }
                    dimensionAndPosition.setImageX(x);
                    dimensionAndPosition.setImageY(y);
                    dimensionAndPosition.setImageWidth(width);
                    dimensionAndPosition.setImageHeight(height);
                    break;

                case CENTER:
                    dimensionAndPosition.setImageX(dimensionAndPosition.getImageBoxX() +
                            (dimensionAndPosition.getImageBoxWidth() - dimensionAndPosition.getImageWidth()) / 2f);
                    dimensionAndPosition.setImageY(dimensionAndPosition.getImageBoxY() +
                            (dimensionAndPosition.getImageBoxHeight() - dimensionAndPosition.getImageHeight()) / 2f);
                    break;

                default:
                    throw new IllegalArgumentException(String.format("The ImageScaling '%s' is not supported!",
                            imageParameters.getImageScaling()));
            }
        }
    }

    private void alignHorizontally() {
        float boxWidth = dimensionAndPosition.getBoxWidth();
        if (ImageRotationUtils.isSwapOfDimensionsRequired(dimensionAndPosition.getGlobalRotation())) {
            boxWidth = dimensionAndPosition.getBoxHeight();
        }
        float boxX;
        SignatureFieldParameters fieldParameters = imageParameters.getFieldParameters();

        VisualSignatureAlignmentHorizontal alignmentHorizontal = imageParameters
                .getVisualSignatureAlignmentHorizontal();
        switch (alignmentHorizontal) {
            case LEFT:
            case NONE:
                boxX = fieldParameters.getOriginX();
                break;
            case CENTER:
                boxX = (pageBox.getWidth() - boxWidth) / 2;
                break;
            case RIGHT:
                boxX = pageBox.getWidth() - boxWidth - fieldParameters.getOriginX();
                break;
            default:
                throw new IllegalStateException(NOT_SUPPORTED_HORIZONTAL_ALIGNMENT_ERROR_MESSAGE + alignmentHorizontal);
        }
        dimensionAndPosition.setBoxX(boxX);
    }

    private void alignVertically() {
        float boxHeight = dimensionAndPosition.getBoxHeight();
        if (ImageRotationUtils.isSwapOfDimensionsRequired(dimensionAndPosition.getGlobalRotation())) {
            boxHeight = dimensionAndPosition.getBoxWidth();
        }
        float boxY;
        SignatureFieldParameters fieldParameters = imageParameters.getFieldParameters();

        VisualSignatureAlignmentVertical alignmentVertical = imageParameters.getVisualSignatureAlignmentVertical();
        switch (alignmentVertical) {
            case TOP:
            case NONE:
                boxY = fieldParameters.getOriginY();
                break;
            case MIDDLE:
                boxY = (pageBox.getHeight() - boxHeight) / 2;
                break;
            case BOTTOM:
                boxY = pageBox.getHeight() - boxHeight - fieldParameters.getOriginY();
                break;
            default:
                throw new IllegalStateException(NOT_SUPPORTED_VERTICAL_ALIGNMENT_ERROR_MESSAGE + alignmentVertical);
        }
        dimensionAndPosition.setBoxY(boxY);
    }

    private void rotateSignatureField() {
        switch (dimensionAndPosition.getGlobalRotation()) {
            case ImageRotationUtils.ANGLE_90:
                float boxX = dimensionAndPosition.getBoxX();
                dimensionAndPosition.setBoxX(
                        pageBox.getHeight() - dimensionAndPosition.getBoxY() - dimensionAndPosition.getBoxWidth());
                dimensionAndPosition.setBoxY(boxX);
                break;
            case ImageRotationUtils.ANGLE_180:
                dimensionAndPosition.setBoxX(
                        pageBox.getWidth() - dimensionAndPosition.getBoxX() - dimensionAndPosition.getBoxWidth());
                dimensionAndPosition.setBoxY(
                        pageBox.getHeight() - dimensionAndPosition.getBoxY() - dimensionAndPosition.getBoxHeight());
                break;
            case ImageRotationUtils.ANGLE_270:
                boxX = dimensionAndPosition.getBoxX();
                dimensionAndPosition.setBoxX(dimensionAndPosition.getBoxY());
                dimensionAndPosition.setBoxY(pageBox.getWidth() - boxX - dimensionAndPosition.getBoxHeight());
                break;
            case ImageRotationUtils.ANGLE_360:
            case ImageRotationUtils.ANGLE_0:
                // do nothing
                break;
            default:
                throw new IllegalStateException(ImageRotationUtils.SUPPORTED_ANGLES_ERROR_MESSAGE);
        }
    }

}