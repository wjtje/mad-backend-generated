import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { RouteComponent } from './list/route.component';
import { RouteDetailComponent } from './detail/route-detail.component';
import { RouteUpdateComponent } from './update/route-update.component';
import RouteResolve from './route/route-routing-resolve.service';

const routeRoute: Routes = [
  {
    path: '',
    component: RouteComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RouteDetailComponent,
    resolve: {
      route: RouteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RouteUpdateComponent,
    resolve: {
      route: RouteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RouteUpdateComponent,
    resolve: {
      route: RouteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default routeRoute;
