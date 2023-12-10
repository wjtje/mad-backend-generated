import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IInspectionPhoto } from '../inspection-photo.model';
import { InspectionPhotoService } from '../service/inspection-photo.service';

export const inspectionPhotoResolve = (route: ActivatedRouteSnapshot): Observable<null | IInspectionPhoto> => {
  const id = route.params['id'];
  if (id) {
    return inject(InspectionPhotoService)
      .find(id)
      .pipe(
        mergeMap((inspectionPhoto: HttpResponse<IInspectionPhoto>) => {
          if (inspectionPhoto.body) {
            return of(inspectionPhoto.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default inspectionPhotoResolve;
