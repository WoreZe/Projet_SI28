import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Story1Component } from './story1.component';

describe('Story1Component', () => {
  let component: Story1Component;
  let fixture: ComponentFixture<Story1Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Story1Component]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(Story1Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
