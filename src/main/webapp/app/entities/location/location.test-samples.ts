import { ILocation, NewLocation } from './location.model';

export const sampleWithRequiredData: ILocation = {
  id: 3710,
};

export const sampleWithPartialData: ILocation = {
  id: 28870,
  city: 'Willmsview',
  stateProvince: 'including greedily',
};

export const sampleWithFullData: ILocation = {
  id: 1710,
  streetAddress: 'meteorology',
  postalCode: 'strafe before oh',
  city: 'Tempe',
  stateProvince: 'ugh',
};

export const sampleWithNewData: NewLocation = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
