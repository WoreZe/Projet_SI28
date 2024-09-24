import {CanActivateFn, Router, Routes} from '@angular/router';
import {StoryComponent} from "./components/story/story.component";
import {Story1Component} from "./components/story1/story1.component";
import {LoginComponent} from "./components/login/login.component";
import {Story2Component} from "./components/story2/story2.component";
import {Story3Component} from "./components/story3/story3.component";
import {Story5Component} from "./components/story5/story5.component";
import {Story4Component} from "./components/story4/story4.component";
import {PageNotFoundComponent} from "./components/page-not-found/page-not-found.component";
import {WaitingForPlayer} from "./components/player/waiting-for-player.component";
import {StoryEditorComponent} from "./components/admin/story-editor-component/story-editor.component";
import {inject} from "@angular/core";
import {CurrentStoryEditService} from "./services/current-story-edit.service";
import {catchError, map, of} from "rxjs";


function setCurrentStory(): CanActivateFn {
  return (route, state) => {
    const storyId = route.params['id'];
    const parsedStoryId = parseInt(storyId, 10);
    if (isNaN(parsedStoryId)) {
      return of(false);
    }
    const service = inject(CurrentStoryEditService);
    const router = inject(Router);
    return service.getHistory(parsedStoryId).pipe(catchError(() => {
      router.navigate(['not-found'], {skipLocationChange: true});
      return of(false);
    }),map(() => true));
  }
}

export const routes: Routes = [
  {
    path: 'viewer',
    children: [
      {path: 'login', component: LoginComponent},
      {path: 'story', component: StoryComponent},
      {path: 'players', component: WaitingForPlayer},
      {path: '', redirectTo: 'login', pathMatch: 'full'}
    ]
  },
  {
    path: 'admin',
    children: [
      {
        path: 'history/:id/editor',
        canActivate: [
         setCurrentStory()
        ],
        component: StoryEditorComponent
      }
    ]
  },
  {path: 'story', component: StoryComponent},
  {path: 'story1', component: Story1Component},
  {path: 'story2', component: Story2Component},
  {path: 'story3', component: Story3Component},
  {path: 'story4', component: Story4Component},
  {path: 'story5', component: Story5Component},
  {path: '**', component: PageNotFoundComponent},
];
