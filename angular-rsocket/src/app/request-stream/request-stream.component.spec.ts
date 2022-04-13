import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestStreamComponent } from './request-stream.component';

describe('RequestStreamComponent', () => {
  let component: RequestStreamComponent;
  let fixture: ComponentFixture<RequestStreamComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RequestStreamComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestStreamComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
