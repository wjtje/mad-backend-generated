import dayjs from 'dayjs/esm';

import { IRoute, NewRoute } from './route.model';

export const sampleWithRequiredData: IRoute = {
  id: 4856,
};

export const sampleWithPartialData: IRoute = {
  id: 7915,
};

export const sampleWithFullData: IRoute = {
  id: 17385,
  code: 'which last',
  description: 'freely next zowie',
  date: dayjs('2023-12-06'),
};

export const sampleWithNewData: NewRoute = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
