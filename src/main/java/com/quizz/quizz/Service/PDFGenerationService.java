package com.quizz.quizz.Service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.property.TextAlignment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;

@Service
public class PDFGenerationService {

    public byte[] generateCertificate(String name, String courseName) throws IOException {
        // Prepare the PDF document
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Adding custom font
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont bodyFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        // Set a background image or border (optional)
        // Example: document.add(new Image(...)); // For adding logo

        // Title section - Certificate title
        document.add(new Paragraph("Certificate of Completion")
                .setFont(font)
                .setFontSize(24)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontColor(ColorConstants.DARK_GRAY));

        document.add(new Paragraph("This is to certify that")
                .setFont(bodyFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.BLACK));

        // Name section
        document.add(new Paragraph(name)
                .setFont(font)
                .setFontSize(28)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontColor(ColorConstants.BLUE));

        // Course section
        document.add(new Paragraph("has successfully completed the course")
                .setFont(bodyFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.BLACK));

        document.add(new Paragraph(courseName)
                .setFont(font)
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GREEN));

        // Date section
        document.add(new Paragraph("Date: " + LocalDate.now())
                .setFont(bodyFont)
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY));

        // Footer (optional)
        document.add(new Paragraph("Authorized Signature")
                .setFont(bodyFont)
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.BLACK));

        // Closing the document
        document.close();

        return byteArrayOutputStream.toByteArray();
    }


}
