import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRental } from '../rental.model';
import { RentalService } from '../service/rental.service';

export const rentalResolve = (route: ActivatedRouteSnapshot): Observable<null | IRental> => {
  const id = route.params['id'];
  if (id) {
    return inject(RentalService)
      .find(id)
      .pipe(
        mergeMap((rental: HttpResponse<IRental>) => {
          if (rental.body) {
            return of(rental.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default rentalResolve;
