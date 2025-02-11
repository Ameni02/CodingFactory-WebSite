package com.esprit.microservice.pfespace.Services;

import com.esprit.microservice.pfespace.Repositories.DeliverableRepo;
import com.esprit.microservice.pfespace.Repositories.EvaluationRepo;
import com.esprit.microservice.pfespace.Repositories.ProjectRepo;
import org.springframework.stereotype.Service;

@Service

public class PfeServiceImp {
    DeliverableRepo deliverableRepo;
    EvaluationRepo evaluationRepo;
    ProjectRepo projectRepo;
}
