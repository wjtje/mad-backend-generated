import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'car',
        data: { pageTitle: 'autoMaatApp.car.home.title' },
        loadChildren: () => import('./car/car.routes'),
      },
      {
        path: 'rental',
        data: { pageTitle: 'autoMaatApp.rental.home.title' },
        loadChildren: () => import('./rental/rental.routes'),
      },
      {
        path: 'customer',
        data: { pageTitle: 'autoMaatApp.customer.home.title' },
        loadChildren: () => import('./customer/customer.routes'),
      },
      {
        path: 'employee',
        data: { pageTitle: 'autoMaatApp.employee.home.title' },
        loadChildren: () => import('./employee/employee.routes'),
      },
      {
        path: 'inspection',
        data: { pageTitle: 'autoMaatApp.inspection.home.title' },
        loadChildren: () => import('./inspection/inspection.routes'),
      },
      {
        path: 'inspection-photo',
        data: { pageTitle: 'autoMaatApp.inspectionPhoto.home.title' },
        loadChildren: () => import('./inspection-photo/inspection-photo.routes'),
      },
      {
        path: 'repair',
        data: { pageTitle: 'autoMaatApp.repair.home.title' },
        loadChildren: () => import('./repair/repair.routes'),
      },
      {
        path: 'location',
        data: { pageTitle: 'autoMaatApp.location.home.title' },
        loadChildren: () => import('./location/location.routes'),
      },
      {
        path: 'route',
        data: { pageTitle: 'autoMaatApp.route.home.title' },
        loadChildren: () => import('./route/route.routes'),
      },
      {
        path: 'route-stop',
        data: { pageTitle: 'autoMaatApp.routeStop.home.title' },
        loadChildren: () => import('./route-stop/route-stop.routes'),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
