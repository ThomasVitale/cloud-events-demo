import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestResponseComponent } from './request-response.component';

describe('RequestResponseComponent', () => {
  let component: RequestResponseComponent;
  let fixture: ComponentFixture<RequestResponseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RequestResponseComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestResponseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
