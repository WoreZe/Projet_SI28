import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Story2Component } from './story2.component';

describe('Story2Component', () => {
  let component: Story2Component;
  let fixture: ComponentFixture<Story2Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Story2Component]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(Story2Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
