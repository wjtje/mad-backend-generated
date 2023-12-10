import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { RouteDetailComponent } from './route-detail.component';

describe('Route Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouteDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: RouteDetailComponent,
              resolve: { route: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(RouteDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load route on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', RouteDetailComponent);

      // THEN
      expect(instance.route).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
