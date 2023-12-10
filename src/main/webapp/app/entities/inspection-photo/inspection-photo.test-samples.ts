import { IInspectionPhoto, NewInspectionPhoto } from './inspection-photo.model';

export const sampleWithRequiredData: IInspectionPhoto = {
  id: 9616,
};

export const sampleWithPartialData: IInspectionPhoto = {
  id: 13663,
};

export const sampleWithFullData: IInspectionPhoto = {
  id: 19814,
  photo: '../fake-data/blob/hipster.png',
  photoContentType: 'unknown',
};

export const sampleWithNewData: NewInspectionPhoto = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
