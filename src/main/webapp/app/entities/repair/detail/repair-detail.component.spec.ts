import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { RepairDetailComponent } from './repair-detail.component';

describe('Repair Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RepairDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: RepairDetailComponent,
              resolve: { repair: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(RepairDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load repair on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', RepairDetailComponent);

      // THEN
      expect(instance.repair).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
