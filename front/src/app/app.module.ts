import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { AddQuizComponent } from './quiz/add-quiz/add-quiz.component';
import { QuizListWitoutQuestionsComponent } from './quiz/quiz-list-without-questions/quiz-list-without-questions.component';
import { QuizDetailsComponent } from './quiz-details/quiz-details.component';
import { TakeQuizComponent } from './take-quiz/take-quiz.component';
import { QuizListComponent } from './quiz-list/quiz-list.component';
import { AiQuizSuggestionsComponent } from './components/ai-quiz-suggestions/ai-quiz-suggestions.component';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

// ✅ MODULES ANGULAR MATERIAL
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatRadioModule } from '@angular/material/radio';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { MatStepperModule } from '@angular/material/stepper';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSelectModule } from '@angular/material/select';
import { QuizResultComponent } from './quiz-result/quiz-result.component';
import { UserStatsComponent } from './user-stats/user-stats.component';
import { MlguesserComponent } from './mlguesser/mlguesser.component';






@NgModule({
  declarations: [
    AppComponent,
    AddQuizComponent, // ✅ Composant classique
    QuizListWitoutQuestionsComponent,
    QuizDetailsComponent,
    TakeQuizComponent,
    QuizListComponent,
    AiQuizSuggestionsComponent,
    QuizResultComponent,
    UserStatsComponent,
    MlguesserComponent
    
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    

    // ✅ IMPORTS MATERIAL
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCheckboxModule,
    MatIconModule,
    MatCardModule,
    MatListModule,
    MatExpansionModule,
    MatRadioModule,
    MatPaginatorModule,
    MatTableModule,
    MatStepperModule,
    MatSnackBarModule,
    MatSelectModule,
   

  ],
  providers: [],
  bootstrap: [AppComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA] // Pour certains composants personnalisés
})
export class AppModule {}
