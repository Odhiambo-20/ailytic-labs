package com.payment.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * QR Code Generator Utility for Payment System
 * Generates WeChat-style QR codes for payments with customization options
 */
@Component
public class QRCodeGenerator {

    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 300;
    private static final String DEFAULT_FORMAT = "PNG";
    private static final int DEFAULT_MARGIN = 1;
    
    /**
     * Generate QR code as BufferedImage
     * @param content QR code content
     * @param width Image width
     * @param height Image height
     * @return BufferedImage containing QR code
     * @throws WriterException if QR generation fails
     */
    public BufferedImage generateQRCodeImage(String content, int width, int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = getDefaultHints();
        
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
    
    /**
     * Generate QR code with custom colors
     * @param content QR code content
     * @param width Image width
     * @param height Image height
     * @param foregroundColor Foreground color (hex)
     * @param backgroundColor Background color (hex)
     * @return BufferedImage containing colored QR code
     * @throws WriterException if QR generation fails
     */
    public BufferedImage generateColoredQRCode(
            String content, 
            int width, 
            int height,
            String foregroundColor,
            String backgroundColor) throws WriterException {
        
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = getDefaultHints();
        
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        
        // Convert hex colors to RGB
        int fgColor = Color.decode(foregroundColor).getRGB();
        int bgColor = Color.decode(backgroundColor).getRGB();
        
        MatrixToImageConfig config = new MatrixToImageConfig(fgColor, bgColor);
        return MatrixToImageWriter.toBufferedImage(bitMatrix, config);
    }
    
    /**
     * Generate QR code with logo overlay (WeChat-style)
     * @param content QR code content
     * @param width Image width
     * @param height Image height
     * @param logoPath Path to logo image
     * @param logoSize Size of the logo
     * @return BufferedImage containing QR code with logo
     * @throws WriterException if QR generation fails
     * @throws IOException if logo loading fails
     */
    public BufferedImage generateQRCodeWithLogo(
            String content,
            int width,
            int height,
            String logoPath,
            int logoSize) throws WriterException, IOException {
        
        // Generate base QR code
        BufferedImage qrImage = generateQRCodeImage(content, width, height);
        
        // Load logo
        BufferedImage logo = ImageIO.read(new File(logoPath));
        
        // Resize logo if necessary
        if (logo.getWidth() != logoSize || logo.getHeight() != logoSize) {
            logo = resizeImage(logo, logoSize, logoSize);
        }
        
        // Overlay logo on QR code (centered)
        return overlayLogo(qrImage, logo);
    }
    
    /**
     * Generate QR code and return as Base64 string
     * @param content QR code content
     * @param width Image width
     * @param height Image height
     * @return Base64 encoded QR code image
     * @throws WriterException if QR generation fails
     * @throws IOException if image conversion fails
     */
    public String generateQRCodeBase64(String content, int width, int height) 
            throws WriterException, IOException {
        
        BufferedImage qrImage = generateQRCodeImage(content, width, height);
        return imageToBase64(qrImage, DEFAULT_FORMAT);
    }
    
    /**
     * Generate QR code with default dimensions
     * @param content QR code content
     * @return Base64 encoded QR code image
     * @throws WriterException if QR generation fails
     * @throws IOException if image conversion fails
     */
    public String generateQRCodeBase64(String content) throws WriterException, IOException {
        return generateQRCodeBase64(content, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    
    /**
     * Generate QR code and save to file
     * @param content QR code content
     * @param width Image width
     * @param height Image height
     * @param filePath Output file path
     * @throws WriterException if QR generation fails
     * @throws IOException if file writing fails
     */
    public void generateQRCodeToFile(String content, int width, int height, String filePath) 
            throws WriterException, IOException {
        
        BufferedImage qrImage = generateQRCodeImage(content, width, height);
        File outputFile = new File(filePath);
        ImageIO.write(qrImage, DEFAULT_FORMAT, outputFile);
    }
    
    /**
     * Generate QR code with payment information (WeChat-style format)
     * @param paymentId Payment identifier
     * @param merchantId Merchant identifier
     * @param amount Payment amount
     * @param currency Currency code
     * @param expiryTimestamp Expiry timestamp
     * @return Base64 encoded QR code
     * @throws WriterException if QR generation fails
     * @throws IOException if image conversion fails
     */
    public String generatePaymentQRCode(
            String paymentId,
            String merchantId,
            double amount,
            String currency,
            long expiryTimestamp) throws WriterException, IOException {
        
        // Create JSON payload for QR code
        String content = String.format(
            "{\"id\":\"%s\",\"merchant\":\"%s\",\"amount\":%.2f,\"currency\":\"%s\",\"exp\":%d}",
            paymentId, merchantId, amount, currency, expiryTimestamp
        );
        
        return generateQRCodeBase64(content);
    }
    
    /**
     * Generate QR code with URL format
     * @param baseUrl Base URL for payment processing
     * @param paymentId Payment identifier
     * @return Base64 encoded QR code
     * @throws WriterException if QR generation fails
     * @throws IOException if image conversion fails
     */
    public String generatePaymentQRCodeURL(String baseUrl, String paymentId) 
            throws WriterException, IOException {
        
        String url = baseUrl + "/payment/qr/" + paymentId;
        return generateQRCodeBase64(url);
    }
    
    /**
     * Validate QR code content length
     * @param content QR code content
     * @return true if content is valid
     */
    public boolean isContentValid(String content) {
        if (content == null || content.isEmpty()) {
            return false;
        }
        
        // QR codes can hold up to ~4,296 alphanumeric characters
        // For payments, we keep it much smaller for faster scanning
        return content.length() <= 1000;
    }
    
    /**
     * Get recommended QR code size based on content length
     * @param contentLength Length of content
     * @return Recommended size in pixels
     */
    public int getRecommendedSize(int contentLength) {
        if (contentLength < 50) {
            return 200;
        } else if (contentLength < 100) {
            return 250;
        } else if (contentLength < 200) {
            return 300;
        } else if (contentLength < 500) {
            return 400;
        } else {
            return 500;
        }
    }
    
    /**
     * Get default encoding hints for QR code generation
     * @return Map of encoding hints
     */
    private Map<EncodeHintType, Object> getDefaultHints() {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // Highest error correction
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, DEFAULT_MARGIN);
        return hints;
    }
    
    /**
     * Convert BufferedImage to Base64 string
     * @param image BufferedImage to convert
     * @param format Image format (PNG, JPEG, etc.)
     * @return Base64 encoded string
     * @throws IOException if conversion fails
     */
    private String imageToBase64(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }
    
    /**
     * Resize image to specified dimensions
     * @param originalImage Original image
     * @param targetWidth Target width
     * @param targetHeight Target height
     * @return Resized image
     */
    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = resizedImage.createGraphics();
        
        // Enable high-quality rendering
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        graphics.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics.dispose();
        
        return resizedImage;
    }
    
    /**
     * Overlay logo image on QR code (centered)
     * @param qrImage QR code image
     * @param logo Logo image
     * @return Combined image
     */
    private BufferedImage overlayLogo(BufferedImage qrImage, BufferedImage logo) {
        int qrWidth = qrImage.getWidth();
        int qrHeight = qrImage.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();
        
        // Calculate position to center logo
        int x = (qrWidth - logoWidth) / 2;
        int y = (qrHeight - logoHeight) / 2;
        
        // Create combined image
        BufferedImage combined = new BufferedImage(qrWidth, qrHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = combined.createGraphics();
        
        // Draw QR code
        graphics.drawImage(qrImage, 0, 0, null);
        
        // Draw white background for logo (for better visibility)
        int padding = 5;
        graphics.setColor(Color.WHITE);
        graphics.fillRect(x - padding, y - padding, logoWidth + (padding * 2), logoHeight + (padding * 2));
        
        // Draw logo
        graphics.drawImage(logo, x, y, null);
        graphics.dispose();
        
        return combined;
    }
    
    /**
     * Generate QR code with rounded corners
     * @param content QR code content
     * @param width Image width
     * @param height Image height
     * @param cornerRadius Corner radius in pixels
     * @return BufferedImage with rounded corners
     * @throws WriterException if QR generation fails
     */
    public BufferedImage generateRoundedQRCode(
            String content,
            int width,
            int height,
            int cornerRadius) throws WriterException {
        
        BufferedImage qrImage = generateQRCodeImage(content, width, height);
        return makeRoundedCorner(qrImage, cornerRadius);
    }
    
    /**
     * Make image corners rounded
     * @param image Original image
     * @param cornerRadius Radius of corners
     * @return Image with rounded corners
     */
    private BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        int width = image.getWidth();
        int height = image.getHeight();
        
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = output.createGraphics();
        
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(Color.WHITE);
        graphics.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);
        graphics.setComposite(AlphaComposite.SrcIn);
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        
        return output;
    }
}