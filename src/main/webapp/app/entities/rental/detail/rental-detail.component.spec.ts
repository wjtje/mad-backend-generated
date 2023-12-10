import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { RentalDetailComponent } from './rental-detail.component';

describe('Rental Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RentalDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: RentalDetailComponent,
              resolve: { rental: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(RentalDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load rental on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', RentalDetailComponent);

      // THEN
      expect(instance.rental).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
