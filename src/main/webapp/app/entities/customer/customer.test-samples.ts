import dayjs from 'dayjs/esm';

import { ICustomer, NewCustomer } from './customer.model';

export const sampleWithRequiredData: ICustomer = {
  id: 6486,
};

export const sampleWithPartialData: ICustomer = {
  id: 16460,
  lastName: 'Ratke',
  from: dayjs('2023-12-07'),
};

export const sampleWithFullData: ICustomer = {
  id: 616,
  nr: 21630,
  lastName: 'Bednar',
  firstName: 'Robert',
  from: dayjs('2023-12-07'),
};

export const sampleWithNewData: NewCustomer = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
