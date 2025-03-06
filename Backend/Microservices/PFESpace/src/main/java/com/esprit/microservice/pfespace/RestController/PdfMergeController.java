package com.esprit.microservice.pfespace.RestController;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/pdf")
public class PdfMergeController {

    @PostMapping("/merge")
    @CrossOrigin(origins = "http://localhost:4200") // Allow CORS for this method only
    public ResponseEntity<byte[]> mergePdfFiles(@RequestParam("files") List<MultipartFile> files) {
        if (files.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        try (ByteArrayOutputStream mergedOutputStream = new ByteArrayOutputStream()) {
            PDFMergerUtility merger = new PDFMergerUtility();
            merger.setDestinationStream(mergedOutputStream);

            for (MultipartFile file : files) {
                merger.addSource(file.getInputStream());
            }

            merger.mergeDocuments(null);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Merged_Deliverable.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(mergedOutputStream.toByteArray());

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
