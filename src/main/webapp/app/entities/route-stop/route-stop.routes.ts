import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { RouteStopComponent } from './list/route-stop.component';
import { RouteStopDetailComponent } from './detail/route-stop-detail.component';
import { RouteStopUpdateComponent } from './update/route-stop-update.component';
import RouteStopResolve from './route/route-stop-routing-resolve.service';

const routeStopRoute: Routes = [
  {
    path: '',
    component: RouteStopComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RouteStopDetailComponent,
    resolve: {
      routeStop: RouteStopResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RouteStopUpdateComponent,
    resolve: {
      routeStop: RouteStopResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RouteStopUpdateComponent,
    resolve: {
      routeStop: RouteStopResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default routeStopRoute;
