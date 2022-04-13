import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FireAndForgetComponent } from './fire-and-forget.component';

describe('FireAndForgetComponent', () => {
  let component: FireAndForgetComponent;
  let fixture: ComponentFixture<FireAndForgetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FireAndForgetComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FireAndForgetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
