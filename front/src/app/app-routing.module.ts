import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AddQuizComponent } from './quiz/add-quiz/add-quiz.component';
import { QuizListWitoutQuestionsComponent } from './quiz/quiz-list-without-questions/quiz-list-without-questions.component';
import { QuizDetailsComponent } from './quiz-details/quiz-details.component';
import {TakeQuizComponent} from './take-quiz/take-quiz.component';
import { QuizListComponent } from './quiz-list/quiz-list.component';
import { QuizResultComponent } from './quiz-result/quiz-result.component';
import { UserStatsComponent } from './user-stats/user-stats.component';
import { MlguesserComponent } from './mlguesser/mlguesser.component';
const routes: Routes = [
  { path: 'add-quiz', component: AddQuizComponent },
  {path: 'quiz-list', component: QuizListWitoutQuestionsComponent },
  { path: 'quiz-details/:id', component: QuizDetailsComponent },
  { path: 'quizzes', component: QuizListComponent },
  { path: 'take-quiz/:id', component: TakeQuizComponent },
  { path: 'quiz-result/:id', component: QuizResultComponent},
  { path: 'user-stats', component: UserStatsComponent },
  { path: 'categoryguess', component: MlguesserComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { 

 }
