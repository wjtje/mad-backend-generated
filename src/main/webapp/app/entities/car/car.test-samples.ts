import dayjs from 'dayjs/esm';

import { ICar, NewCar } from './car.model';

export const sampleWithRequiredData: ICar = {
  id: 5541,
};

export const sampleWithPartialData: ICar = {
  id: 2081,
  model: 'meanwhile pumpkinseed inside',
  engineSize: 20138,
  price: 8551.34,
  longitude: 21341.92,
  latitude: 30557.23,
};

export const sampleWithFullData: ICar = {
  id: 23876,
  brand: 'realign',
  model: 'towards excitedly zowie',
  fuel: 'DIESEL',
  options: 'transform fiercely after',
  licensePlate: 'pointed',
  engineSize: 2914,
  modelYear: 29490,
  since: dayjs('2023-12-07'),
  price: 24218.7,
  nrOfSeats: 24836,
  body: 'SEDAN',
  longitude: 11259.25,
  latitude: 1234.27,
};

export const sampleWithNewData: NewCar = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
