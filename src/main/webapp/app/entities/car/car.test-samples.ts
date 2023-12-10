import dayjs from 'dayjs/esm';

import { ICar, NewCar } from './car.model';

export const sampleWithRequiredData: ICar = {
  id: 19080,
};

export const sampleWithPartialData: ICar = {
  id: 23074,
  brand: 'whistle or masculinise',
  model: 'er save',
  options: 'when phew',
  since: dayjs('2023-12-07'),
  body: 'SEDAN',
};

export const sampleWithFullData: ICar = {
  id: 31464,
  brand: 'unto',
  model: 'grab broadly',
  fuel: 'HYBRID',
  options: 'molding while that',
  licensePlate: 'reproachfully of',
  engineSize: 14365,
  modelYear: 5490,
  since: dayjs('2023-12-06'),
  price: 27445.95,
  nrOfSeats: 24481,
  body: 'SUV',
};

export const sampleWithNewData: NewCar = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
