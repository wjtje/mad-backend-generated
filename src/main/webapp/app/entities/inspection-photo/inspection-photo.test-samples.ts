import { IInspectionPhoto, NewInspectionPhoto } from './inspection-photo.model';

export const sampleWithRequiredData: IInspectionPhoto = {
  id: 11588,
};

export const sampleWithPartialData: IInspectionPhoto = {
  id: 30915,
  photo: '../fake-data/blob/hipster.png',
  photoContentType: 'unknown',
};

export const sampleWithFullData: IInspectionPhoto = {
  id: 13967,
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
