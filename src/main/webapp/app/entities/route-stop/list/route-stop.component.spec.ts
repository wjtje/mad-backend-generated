import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { RouteStopService } from '../service/route-stop.service';

import { RouteStopComponent } from './route-stop.component';

describe('RouteStop Management Component', () => {
  let comp: RouteStopComponent;
  let fixture: ComponentFixture<RouteStopComponent>;
  let service: RouteStopService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'route-stop', component: RouteStopComponent }]),
        HttpClientTestingModule,
        RouteStopComponent,
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
      .overrideTemplate(RouteStopComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RouteStopComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(RouteStopService);

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
    expect(comp.routeStops?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to routeStopService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getRouteStopIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getRouteStopIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
