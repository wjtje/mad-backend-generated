import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { InspectionPhotoService } from '../service/inspection-photo.service';

import { InspectionPhotoComponent } from './inspection-photo.component';

describe('InspectionPhoto Management Component', () => {
  let comp: InspectionPhotoComponent;
  let fixture: ComponentFixture<InspectionPhotoComponent>;
  let service: InspectionPhotoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'inspection-photo', component: InspectionPhotoComponent }]),
        HttpClientTestingModule,
        InspectionPhotoComponent,
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
      .overrideTemplate(InspectionPhotoComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(InspectionPhotoComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(InspectionPhotoService);

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
    expect(comp.inspectionPhotos?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to inspectionPhotoService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getInspectionPhotoIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getInspectionPhotoIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
