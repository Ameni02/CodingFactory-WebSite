package com.quizz.quizz.Controller;



import com.quizz.quizz.Dto.CorrectionRequest;
import com.quizz.quizz.Dto.CorrectionResponse;
import com.quizz.quizz.Service.CorrectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/correction")

public class CorrectionController {

    @Autowired
    private CorrectionService correctionService;

    @PostMapping
    public CorrectionResponse correctQuiz(@RequestBody CorrectionRequest request) {
        return correctionService.evaluateQuiz(request);
    }
}
