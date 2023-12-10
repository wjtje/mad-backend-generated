import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { RouteStopDetailComponent } from './route-stop-detail.component';

describe('RouteStop Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouteStopDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: RouteStopDetailComponent,
              resolve: { routeStop: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(RouteStopDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load routeStop on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', RouteStopDetailComponent);

      // THEN
      expect(instance.routeStop).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
