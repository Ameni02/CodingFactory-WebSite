package org.esprit.gestion_user.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPredictionRequest {
    private int assiduite;
    private double noteProjet;
    private double exams;
    private double cc;
}