import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { RepairComponent } from './list/repair.component';
import { RepairDetailComponent } from './detail/repair-detail.component';
import { RepairUpdateComponent } from './update/repair-update.component';
import RepairResolve from './route/repair-routing-resolve.service';

const repairRoute: Routes = [
  {
    path: '',
    component: RepairComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RepairDetailComponent,
    resolve: {
      repair: RepairResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RepairUpdateComponent,
    resolve: {
      repair: RepairResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RepairUpdateComponent,
    resolve: {
      repair: RepairResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default repairRoute;
