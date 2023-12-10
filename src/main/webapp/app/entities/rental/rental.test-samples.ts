import dayjs from 'dayjs/esm';

import { IRental, NewRental } from './rental.model';

export const sampleWithRequiredData: IRental = {
  id: 20845,
};

export const sampleWithPartialData: IRental = {
  id: 19820,
  longitude: 26902.65,
  latitude: 8848.12,
  toDate: dayjs('2023-12-07'),
};

export const sampleWithFullData: IRental = {
  id: 4530,
  code: 'allow uh-huh glossy',
  longitude: 7049.47,
  latitude: 1866.25,
  fromDate: dayjs('2023-12-07'),
  toDate: dayjs('2023-12-07'),
  state: 'RETURNED',
};

export const sampleWithNewData: NewRental = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
