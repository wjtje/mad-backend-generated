import dayjs from 'dayjs/esm';

import { IRental, NewRental } from './rental.model';

export const sampleWithRequiredData: IRental = {
  id: 10455,
};

export const sampleWithPartialData: IRental = {
  id: 4588,
  code: 'minus yippee',
  longitude: 11067.44,
  latitude: 30104.42,
  fromDate: dayjs('2023-12-07'),
  toDate: dayjs('2023-12-06'),
  state: 'RETURNED',
};

export const sampleWithFullData: IRental = {
  id: 5091,
  code: 'function around',
  longitude: 9453.18,
  latitude: 4967.29,
  fromDate: dayjs('2023-12-07'),
  toDate: dayjs('2023-12-07'),
  state: 'RESERVED',
};

export const sampleWithNewData: NewRental = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
