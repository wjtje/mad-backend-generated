import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IInspection } from '../inspection.model';
import { InspectionService } from '../service/inspection.service';

export const inspectionResolve = (route: ActivatedRouteSnapshot): Observable<null | IInspection> => {
  const id = route.params['id'];
  if (id) {
    return inject(InspectionService)
      .find(id)
      .pipe(
        mergeMap((inspection: HttpResponse<IInspection>) => {
          if (inspection.body) {
            return of(inspection.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default inspectionResolve;
