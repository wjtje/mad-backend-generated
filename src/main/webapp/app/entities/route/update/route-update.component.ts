import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IEmployee } from 'app/entities/employee/employee.model';
import { EmployeeService } from 'app/entities/employee/service/employee.service';
import { IRoute } from '../route.model';
import { RouteService } from '../service/route.service';
import { RouteFormService, RouteFormGroup } from './route-form.service';

@Component({
  standalone: true,
  selector: 'jhi-route-update',
  templateUrl: './route-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class RouteUpdateComponent implements OnInit {
  isSaving = false;
  route: IRoute | null = null;

  employeesSharedCollection: IEmployee[] = [];

  editForm: RouteFormGroup = this.routeFormService.createRouteFormGroup();

  constructor(
    protected routeService: RouteService,
    protected routeFormService: RouteFormService,
    protected employeeService: EmployeeService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareEmployee = (o1: IEmployee | null, o2: IEmployee | null): boolean => this.employeeService.compareEmployee(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ route }) => {
      this.route = route;
      if (route) {
        this.updateForm(route);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const route = this.routeFormService.getRoute(this.editForm);
    if (route.id !== null) {
      this.subscribeToSaveResponse(this.routeService.update(route));
    } else {
      this.subscribeToSaveResponse(this.routeService.create(route));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRoute>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(route: IRoute): void {
    this.route = route;
    this.routeFormService.resetForm(this.editForm, route);

    this.employeesSharedCollection = this.employeeService.addEmployeeToCollectionIfMissing<IEmployee>(
      this.employeesSharedCollection,
      route.employee,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.employeeService
      .query()
      .pipe(map((res: HttpResponse<IEmployee[]>) => res.body ?? []))
      .pipe(
        map((employees: IEmployee[]) => this.employeeService.addEmployeeToCollectionIfMissing<IEmployee>(employees, this.route?.employee)),
      )
      .subscribe((employees: IEmployee[]) => (this.employeesSharedCollection = employees));
  }
}
