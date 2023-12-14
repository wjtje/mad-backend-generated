import dayjs from 'dayjs/esm';

import { IRoute, NewRoute } from './route.model';

export const sampleWithRequiredData: IRoute = {
  id: 177,
};

export const sampleWithPartialData: IRoute = {
  id: 32301,
  code: 'that',
  description: 'supposing while hovel',
  date: dayjs('2023-12-06'),
};

export const sampleWithFullData: IRoute = {
  id: 3981,
  code: 'revolutionise yellow',
  description: 'gah sister canine',
  date: dayjs('2023-12-07'),
};

export const sampleWithNewData: NewRoute = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
