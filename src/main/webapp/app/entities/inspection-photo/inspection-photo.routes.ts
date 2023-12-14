import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { InspectionPhotoComponent } from './list/inspection-photo.component';
import { InspectionPhotoDetailComponent } from './detail/inspection-photo-detail.component';
import { InspectionPhotoUpdateComponent } from './update/inspection-photo-update.component';
import InspectionPhotoResolve from './route/inspection-photo-routing-resolve.service';

const inspectionPhotoRoute: Routes = [
  {
    path: '',
    component: InspectionPhotoComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: InspectionPhotoDetailComponent,
    resolve: {
      inspectionPhoto: InspectionPhotoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: InspectionPhotoUpdateComponent,
    resolve: {
      inspectionPhoto: InspectionPhotoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: InspectionPhotoUpdateComponent,
    resolve: {
      inspectionPhoto: InspectionPhotoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default inspectionPhotoRoute;
