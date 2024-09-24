import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Story4Component } from './story4.component';

describe('Story4Component', () => {
  let component: Story4Component;
  let fixture: ComponentFixture<Story4Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Story4Component]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(Story4Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
