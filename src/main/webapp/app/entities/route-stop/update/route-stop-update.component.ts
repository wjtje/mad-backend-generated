import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IRoute } from 'app/entities/route/route.model';
import { RouteService } from 'app/entities/route/service/route.service';
import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';
import { RouteStopService } from '../service/route-stop.service';
import { IRouteStop } from '../route-stop.model';
import { RouteStopFormService, RouteStopFormGroup } from './route-stop-form.service';

@Component({
  standalone: true,
  selector: 'jhi-route-stop-update',
  templateUrl: './route-stop-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class RouteStopUpdateComponent implements OnInit {
  isSaving = false;
  routeStop: IRouteStop | null = null;

  routesSharedCollection: IRoute[] = [];
  locationsSharedCollection: ILocation[] = [];

  editForm: RouteStopFormGroup = this.routeStopFormService.createRouteStopFormGroup();

  constructor(
    protected routeStopService: RouteStopService,
    protected routeStopFormService: RouteStopFormService,
    protected routeService: RouteService,
    protected locationService: LocationService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareRoute = (o1: IRoute | null, o2: IRoute | null): boolean => this.routeService.compareRoute(o1, o2);

  compareLocation = (o1: ILocation | null, o2: ILocation | null): boolean => this.locationService.compareLocation(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ routeStop }) => {
      this.routeStop = routeStop;
      if (routeStop) {
        this.updateForm(routeStop);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const routeStop = this.routeStopFormService.getRouteStop(this.editForm);
    if (routeStop.id !== null) {
      this.subscribeToSaveResponse(this.routeStopService.update(routeStop));
    } else {
      this.subscribeToSaveResponse(this.routeStopService.create(routeStop));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRouteStop>>): void {
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

  protected updateForm(routeStop: IRouteStop): void {
    this.routeStop = routeStop;
    this.routeStopFormService.resetForm(this.editForm, routeStop);

    this.routesSharedCollection = this.routeService.addRouteToCollectionIfMissing<IRoute>(this.routesSharedCollection, routeStop.route);
    this.locationsSharedCollection = this.locationService.addLocationToCollectionIfMissing<ILocation>(
      this.locationsSharedCollection,
      routeStop.location,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.routeService
      .query()
      .pipe(map((res: HttpResponse<IRoute[]>) => res.body ?? []))
      .pipe(map((routes: IRoute[]) => this.routeService.addRouteToCollectionIfMissing<IRoute>(routes, this.routeStop?.route)))
      .subscribe((routes: IRoute[]) => (this.routesSharedCollection = routes));

    this.locationService
      .query()
      .pipe(map((res: HttpResponse<ILocation[]>) => res.body ?? []))
      .pipe(
        map((locations: ILocation[]) =>
          this.locationService.addLocationToCollectionIfMissing<ILocation>(locations, this.routeStop?.location),
        ),
      )
      .subscribe((locations: ILocation[]) => (this.locationsSharedCollection = locations));
  }
}
