import dayjs from 'dayjs/esm';

import { ICustomer, NewCustomer } from './customer.model';

export const sampleWithRequiredData: ICustomer = {
  id: 32686,
};

export const sampleWithPartialData: ICustomer = {
  id: 5294,
  nr: 20428,
  from: dayjs('2023-12-06'),
};

export const sampleWithFullData: ICustomer = {
  id: 29155,
  nr: 25789,
  lastName: 'Bergstrom',
  firstName: 'Precious',
  from: dayjs('2023-12-06'),
};

export const sampleWithNewData: NewCustomer = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
