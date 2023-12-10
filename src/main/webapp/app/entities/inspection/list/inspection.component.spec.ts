import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { InspectionService } from '../service/inspection.service';

import { InspectionComponent } from './inspection.component';

describe('Inspection Management Component', () => {
  let comp: InspectionComponent;
  let fixture: ComponentFixture<InspectionComponent>;
  let service: InspectionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'inspection', component: InspectionComponent }]),
        HttpClientTestingModule,
        InspectionComponent,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              }),
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(InspectionComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(InspectionComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(InspectionService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        }),
      ),
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.inspections?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to inspectionService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getInspectionIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getInspectionIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
