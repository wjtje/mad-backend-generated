import { ILocation, NewLocation } from './location.model';

export const sampleWithRequiredData: ILocation = {
  id: 27871,
};

export const sampleWithPartialData: ILocation = {
  id: 26361,
  streetAddress: 'plus',
  postalCode: 'whoever',
  city: 'Laurenceburgh',
  stateProvince: 'offer innocently',
};

export const sampleWithFullData: ILocation = {
  id: 26060,
  streetAddress: 'whenever mortar',
  postalCode: 'down crushing far',
  city: 'Dejuanchester',
  stateProvince: 'caution round',
};

export const sampleWithNewData: NewLocation = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
