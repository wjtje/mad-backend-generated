import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IInspection } from 'app/entities/inspection/inspection.model';
import { InspectionService } from 'app/entities/inspection/service/inspection.service';
import { InspectionPhotoService } from '../service/inspection-photo.service';
import { IInspectionPhoto } from '../inspection-photo.model';
import { InspectionPhotoFormService } from './inspection-photo-form.service';

import { InspectionPhotoUpdateComponent } from './inspection-photo-update.component';

describe('InspectionPhoto Management Update Component', () => {
  let comp: InspectionPhotoUpdateComponent;
  let fixture: ComponentFixture<InspectionPhotoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let inspectionPhotoFormService: InspectionPhotoFormService;
  let inspectionPhotoService: InspectionPhotoService;
  let inspectionService: InspectionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), InspectionPhotoUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(InspectionPhotoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(InspectionPhotoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    inspectionPhotoFormService = TestBed.inject(InspectionPhotoFormService);
    inspectionPhotoService = TestBed.inject(InspectionPhotoService);
    inspectionService = TestBed.inject(InspectionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Inspection query and add missing value', () => {
      const inspectionPhoto: IInspectionPhoto = { id: 456 };
      const inspection: IInspection = { id: 15524 };
      inspectionPhoto.inspection = inspection;

      const inspectionCollection: IInspection[] = [{ id: 22136 }];
      jest.spyOn(inspectionService, 'query').mockReturnValue(of(new HttpResponse({ body: inspectionCollection })));
      const additionalInspections = [inspection];
      const expectedCollection: IInspection[] = [...additionalInspections, ...inspectionCollection];
      jest.spyOn(inspectionService, 'addInspectionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ inspectionPhoto });
      comp.ngOnInit();

      expect(inspectionService.query).toHaveBeenCalled();
      expect(inspectionService.addInspectionToCollectionIfMissing).toHaveBeenCalledWith(
        inspectionCollection,
        ...additionalInspections.map(expect.objectContaining),
      );
      expect(comp.inspectionsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const inspectionPhoto: IInspectionPhoto = { id: 456 };
      const inspection: IInspection = { id: 9496 };
      inspectionPhoto.inspection = inspection;

      activatedRoute.data = of({ inspectionPhoto });
      comp.ngOnInit();

      expect(comp.inspectionsSharedCollection).toContain(inspection);
      expect(comp.inspectionPhoto).toEqual(inspectionPhoto);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInspectionPhoto>>();
      const inspectionPhoto = { id: 123 };
      jest.spyOn(inspectionPhotoFormService, 'getInspectionPhoto').mockReturnValue(inspectionPhoto);
      jest.spyOn(inspectionPhotoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inspectionPhoto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: inspectionPhoto }));
      saveSubject.complete();

      // THEN
      expect(inspectionPhotoFormService.getInspectionPhoto).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(inspectionPhotoService.update).toHaveBeenCalledWith(expect.objectContaining(inspectionPhoto));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInspectionPhoto>>();
      const inspectionPhoto = { id: 123 };
      jest.spyOn(inspectionPhotoFormService, 'getInspectionPhoto').mockReturnValue({ id: null });
      jest.spyOn(inspectionPhotoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inspectionPhoto: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: inspectionPhoto }));
      saveSubject.complete();

      // THEN
      expect(inspectionPhotoFormService.getInspectionPhoto).toHaveBeenCalled();
      expect(inspectionPhotoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInspectionPhoto>>();
      const inspectionPhoto = { id: 123 };
      jest.spyOn(inspectionPhotoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inspectionPhoto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(inspectionPhotoService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareInspection', () => {
      it('Should forward to inspectionService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(inspectionService, 'compareInspection');
        comp.compareInspection(entity, entity2);
        expect(inspectionService.compareInspection).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
