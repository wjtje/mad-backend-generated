import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { RentalComponent } from './list/rental.component';
import { RentalDetailComponent } from './detail/rental-detail.component';
import { RentalUpdateComponent } from './update/rental-update.component';
import RentalResolve from './route/rental-routing-resolve.service';

const rentalRoute: Routes = [
  {
    path: '',
    component: RentalComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RentalDetailComponent,
    resolve: {
      rental: RentalResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RentalUpdateComponent,
    resolve: {
      rental: RentalResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RentalUpdateComponent,
    resolve: {
      rental: RentalResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default rentalRoute;
