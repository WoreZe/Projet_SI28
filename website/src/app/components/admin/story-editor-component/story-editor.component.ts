import {Component, HostListener} from '@angular/core';
import {MonacoEditorModule} from "ngx-monaco-editor-v2";
import {FormsModule} from "@angular/forms";
import {HistoryStepFull} from "../../../models/history-instance.model";
import {CurrentStoryEditService} from "../../../services/current-story-edit.service";
import {AsyncPipe} from "@angular/common";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";

@Component({
  selector: 'app-story-editor-component',
  standalone: true,
  imports: [
    MonacoEditorModule,
    FormsModule,
    AsyncPipe,
    RouterLink
  ],
  templateUrl: './story-editor.component.html',
  styleUrl: './story-editor.component.css'
})
export class StoryEditorComponent {
  monacOptionsJs: any = {theme: 'vs-light', language: 'javascript'};
  code: string = 'function x() {\nconsole.log("Hello world!");\n}';
  currentStep?: HistoryStepFull;
  steps$ = this.currentStoryEditService.steps$;
  isHtmlEditing: boolean = false;
  monacOptionsHtml: any = {theme: 'vs-light', language: 'html'};
  currentStory$ = this.currentStoryEditService.currentStory$;
  protected viewPage: "editor" | "history" = "editor";

  constructor(private currentStoryEditService: CurrentStoryEditService, private router: Router, private activatedRoute: ActivatedRoute) {
    this.steps$.subscribe((steps) => {
      if (!this.currentStep) {
        this.currentStep = steps[0];
      }
      activatedRoute.queryParams.subscribe((params) => {
        if (params["s"]) {
          const stepId = parseInt(params["s"], 10);
          this.setStep(steps.find((step) => step.id === stepId));
        }
      });
    });
  }

  isEntryPoint(step: HistoryStepFull) {
    return this.currentStep?.id === step.id;
  }

  setStep(step?: HistoryStepFull) {
    if (!step) {
      return;
    }
    this.currentStep = step;
    if (this.isHtmlEditing) {
      this.code = this.currentStep.page.content;
    } else {
      this.code = this.currentStep.scriptController;
    }
  }

  @HostListener('document:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) {
    if (event.ctrlKey && event.key === 's') {
      event.stopPropagation();
      event.preventDefault();
      this.saveStep();
    }
  }

  saveStep() {
    if (!this.currentStep) {
      return;
    }
    if (!this.isHtmlEditing) {
      this.currentStep.scriptController = this.code;
      this.currentStoryEditService.saveStepScript(this.currentStep).subscribe(() => {
      });
      return;
    } else {
      this.currentStep.page.content = this.code;
      this.currentStoryEditService.saveStepPage(this.currentStep).subscribe(() => {
      });
    }
  }

  selectStep(step: HistoryStepFull) {
    this.saveLocalStep();
    this.router.navigate([], {queryParams: {s: step.id}, queryParamsHandling: 'merge'});
  }

  playStep() {
    if (!this.currentStep) {
      return;
    }
    this.currentStep.scriptController = this.code;
    this.currentStoryEditService.saveStepScript(this.currentStep).subscribe(() => {
      this.currentStoryEditService.runStep(this.currentStep!).subscribe();
    });
  }

  createStep() {

  }

  setHtmlEditing(b: boolean) {
    //is switch from js to html
    if (!this.isHtmlEditing && b) {
      this.currentStep!.scriptController = this.code;
      this.code = this.currentStep!.page.content;
    } else if (this.isHtmlEditing && !b) {
      this.currentStep!.page.content = this.code;
      this.code = this.currentStep!.scriptController;
    }
    this.isHtmlEditing = b;
  }

  private saveLocalStep() {
    if (!this.currentStep) {
      return;
    }
    if (this.isHtmlEditing) {
      this.currentStep.page.content = this.code;
    } else {
      this.currentStep.scriptController = this.code;
    }
  }

  togglePage() {
    if (this.viewPage === "editor") {
      this.viewPage = "history";
    } else {
      this.viewPage = "editor";
    }
  }
}
