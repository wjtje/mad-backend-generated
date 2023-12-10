import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IRoute } from 'app/entities/route/route.model';
import { RouteService } from 'app/entities/route/service/route.service';
import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';
import { IRouteStop } from '../route-stop.model';
import { RouteStopService } from '../service/route-stop.service';
import { RouteStopFormService } from './route-stop-form.service';

import { RouteStopUpdateComponent } from './route-stop-update.component';

describe('RouteStop Management Update Component', () => {
  let comp: RouteStopUpdateComponent;
  let fixture: ComponentFixture<RouteStopUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let routeStopFormService: RouteStopFormService;
  let routeStopService: RouteStopService;
  let routeService: RouteService;
  let locationService: LocationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), RouteStopUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(RouteStopUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RouteStopUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    routeStopFormService = TestBed.inject(RouteStopFormService);
    routeStopService = TestBed.inject(RouteStopService);
    routeService = TestBed.inject(RouteService);
    locationService = TestBed.inject(LocationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Route query and add missing value', () => {
      const routeStop: IRouteStop = { id: 456 };
      const route: IRoute = { id: 18688 };
      routeStop.route = route;

      const routeCollection: IRoute[] = [{ id: 18663 }];
      jest.spyOn(routeService, 'query').mockReturnValue(of(new HttpResponse({ body: routeCollection })));
      const additionalRoutes = [route];
      const expectedCollection: IRoute[] = [...additionalRoutes, ...routeCollection];
      jest.spyOn(routeService, 'addRouteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ routeStop });
      comp.ngOnInit();

      expect(routeService.query).toHaveBeenCalled();
      expect(routeService.addRouteToCollectionIfMissing).toHaveBeenCalledWith(
        routeCollection,
        ...additionalRoutes.map(expect.objectContaining),
      );
      expect(comp.routesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Location query and add missing value', () => {
      const routeStop: IRouteStop = { id: 456 };
      const location: ILocation = { id: 8353 };
      routeStop.location = location;

      const locationCollection: ILocation[] = [{ id: 8432 }];
      jest.spyOn(locationService, 'query').mockReturnValue(of(new HttpResponse({ body: locationCollection })));
      const additionalLocations = [location];
      const expectedCollection: ILocation[] = [...additionalLocations, ...locationCollection];
      jest.spyOn(locationService, 'addLocationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ routeStop });
      comp.ngOnInit();

      expect(locationService.query).toHaveBeenCalled();
      expect(locationService.addLocationToCollectionIfMissing).toHaveBeenCalledWith(
        locationCollection,
        ...additionalLocations.map(expect.objectContaining),
      );
      expect(comp.locationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const routeStop: IRouteStop = { id: 456 };
      const route: IRoute = { id: 30097 };
      routeStop.route = route;
      const location: ILocation = { id: 3389 };
      routeStop.location = location;

      activatedRoute.data = of({ routeStop });
      comp.ngOnInit();

      expect(comp.routesSharedCollection).toContain(route);
      expect(comp.locationsSharedCollection).toContain(location);
      expect(comp.routeStop).toEqual(routeStop);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRouteStop>>();
      const routeStop = { id: 123 };
      jest.spyOn(routeStopFormService, 'getRouteStop').mockReturnValue(routeStop);
      jest.spyOn(routeStopService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ routeStop });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: routeStop }));
      saveSubject.complete();

      // THEN
      expect(routeStopFormService.getRouteStop).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(routeStopService.update).toHaveBeenCalledWith(expect.objectContaining(routeStop));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRouteStop>>();
      const routeStop = { id: 123 };
      jest.spyOn(routeStopFormService, 'getRouteStop').mockReturnValue({ id: null });
      jest.spyOn(routeStopService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ routeStop: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: routeStop }));
      saveSubject.complete();

      // THEN
      expect(routeStopFormService.getRouteStop).toHaveBeenCalled();
      expect(routeStopService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRouteStop>>();
      const routeStop = { id: 123 };
      jest.spyOn(routeStopService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ routeStop });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(routeStopService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareRoute', () => {
      it('Should forward to routeService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(routeService, 'compareRoute');
        comp.compareRoute(entity, entity2);
        expect(routeService.compareRoute).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareLocation', () => {
      it('Should forward to locationService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(locationService, 'compareLocation');
        comp.compareLocation(entity, entity2);
        expect(locationService.compareLocation).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
