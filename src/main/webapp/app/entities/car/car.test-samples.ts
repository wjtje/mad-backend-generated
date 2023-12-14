import dayjs from 'dayjs/esm';

import { ICar, NewCar } from './car.model';

export const sampleWithRequiredData: ICar = {
  id: 12206,
};

export const sampleWithPartialData: ICar = {
  id: 18652,
  fuel: 'GASOLINE',
  since: dayjs('2023-12-07'),
  price: 166.08,
  nrOfSeats: 12322,
};

export const sampleWithFullData: ICar = {
  id: 16274,
  brand: 'or circa wherever',
  model: 'chalk threaten assent',
  fuel: 'HYBRID',
  options: 'damage',
  licensePlate: 'or place',
  engineSize: 17407,
  modelYear: 26127,
  since: dayjs('2023-12-07'),
  price: 430.48,
  nrOfSeats: 5968,
  body: 'SEDAN',
};

export const sampleWithNewData: NewCar = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
