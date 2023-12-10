import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { InspectionComponent } from './list/inspection.component';
import { InspectionDetailComponent } from './detail/inspection-detail.component';
import { InspectionUpdateComponent } from './update/inspection-update.component';
import InspectionResolve from './route/inspection-routing-resolve.service';

const inspectionRoute: Routes = [
  {
    path: '',
    component: InspectionComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: InspectionDetailComponent,
    resolve: {
      inspection: InspectionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: InspectionUpdateComponent,
    resolve: {
      inspection: InspectionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: InspectionUpdateComponent,
    resolve: {
      inspection: InspectionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default inspectionRoute;
