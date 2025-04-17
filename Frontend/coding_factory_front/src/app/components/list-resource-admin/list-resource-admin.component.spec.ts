import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListResourceAdminComponent } from './list-resource-admin.component';

describe('ListResourceAdminComponent', () => {
  let component: ListResourceAdminComponent;
  let fixture: ComponentFixture<ListResourceAdminComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ListResourceAdminComponent]
    });
    fixture = TestBed.createComponent(ListResourceAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
