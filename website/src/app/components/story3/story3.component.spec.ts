import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Story3Component } from './story3.component';

describe('Story3Component', () => {
  let component: Story3Component;
  let fixture: ComponentFixture<Story3Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Story3Component]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(Story3Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
