package com.esprit.microservice.pfespace.Services;

import com.esprit.microservice.pfespace.Entities.Deliverable;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfReportService {

    public byte[] generatePlagiarismReport(Deliverable deliverable) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

                // Add title
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Plagiarism Report for: " + deliverable.getTitle());
                contentStream.endText();

                // Add details
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 650);
                contentStream.showText("Submission Date: " + deliverable.getSubmissionDate());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Plagiarism Score: " + deliverable.getPlagiarismScore() + "%");
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Verdict: " + deliverable.getPlagiarismVerdict());
                contentStream.endText();
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }
}