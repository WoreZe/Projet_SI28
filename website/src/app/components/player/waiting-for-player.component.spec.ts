import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WaitingForPlayer } from './waiting-for-player.component';

describe('LoginComponent', () => {
  let component: WaitingForPlayer;
  let fixture: ComponentFixture<WaitingForPlayer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WaitingForPlayer]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WaitingForPlayer);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
