import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IEmployee } from 'app/entities/employee/employee.model';
import { EmployeeService } from 'app/entities/employee/service/employee.service';
import { RouteService } from '../service/route.service';
import { IRoute } from '../route.model';
import { RouteFormService } from './route-form.service';

import { RouteUpdateComponent } from './route-update.component';

describe('Route Management Update Component', () => {
  let comp: RouteUpdateComponent;
  let fixture: ComponentFixture<RouteUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let routeFormService: RouteFormService;
  let routeService: RouteService;
  let employeeService: EmployeeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), RouteUpdateComponent],
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
      .overrideTemplate(RouteUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RouteUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    routeFormService = TestBed.inject(RouteFormService);
    routeService = TestBed.inject(RouteService);
    employeeService = TestBed.inject(EmployeeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Employee query and add missing value', () => {
      const route: IRoute = { id: 456 };
      const employee: IEmployee = { id: 14845 };
      route.employee = employee;

      const employeeCollection: IEmployee[] = [{ id: 15830 }];
      jest.spyOn(employeeService, 'query').mockReturnValue(of(new HttpResponse({ body: employeeCollection })));
      const additionalEmployees = [employee];
      const expectedCollection: IEmployee[] = [...additionalEmployees, ...employeeCollection];
      jest.spyOn(employeeService, 'addEmployeeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ route });
      comp.ngOnInit();

      expect(employeeService.query).toHaveBeenCalled();
      expect(employeeService.addEmployeeToCollectionIfMissing).toHaveBeenCalledWith(
        employeeCollection,
        ...additionalEmployees.map(expect.objectContaining),
      );
      expect(comp.employeesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const route: IRoute = { id: 456 };
      const employee: IEmployee = { id: 14248 };
      route.employee = employee;

      activatedRoute.data = of({ route });
      comp.ngOnInit();

      expect(comp.employeesSharedCollection).toContain(employee);
      expect(comp.route).toEqual(route);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRoute>>();
      const route = { id: 123 };
      jest.spyOn(routeFormService, 'getRoute').mockReturnValue(route);
      jest.spyOn(routeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ route });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: route }));
      saveSubject.complete();

      // THEN
      expect(routeFormService.getRoute).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(routeService.update).toHaveBeenCalledWith(expect.objectContaining(route));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRoute>>();
      const route = { id: 123 };
      jest.spyOn(routeFormService, 'getRoute').mockReturnValue({ id: null });
      jest.spyOn(routeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ route: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: route }));
      saveSubject.complete();

      // THEN
      expect(routeFormService.getRoute).toHaveBeenCalled();
      expect(routeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRoute>>();
      const route = { id: 123 };
      jest.spyOn(routeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ route });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(routeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareEmployee', () => {
      it('Should forward to employeeService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(employeeService, 'compareEmployee');
        comp.compareEmployee(entity, entity2);
        expect(employeeService.compareEmployee).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
