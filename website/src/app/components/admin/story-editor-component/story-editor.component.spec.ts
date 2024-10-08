import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StoryEditorComponent } from './story-editor.component';

describe('StoryEditorComponentComponent', () => {
  let component: StoryEditorComponent;
  let fixture: ComponentFixture<StoryEditorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StoryEditorComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StoryEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
