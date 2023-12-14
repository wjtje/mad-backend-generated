import dayjs from 'dayjs/esm';

import { IEmployee, NewEmployee } from './employee.model';

export const sampleWithRequiredData: IEmployee = {
  id: 14888,
};

export const sampleWithPartialData: IEmployee = {
  id: 9741,
  nr: 10013,
  lastName: 'Wiegand',
  firstName: 'Hector',
  from: dayjs('2023-12-07'),
};

export const sampleWithFullData: IEmployee = {
  id: 23035,
  nr: 2789,
  lastName: 'McLaughlin',
  firstName: 'Blanche',
  from: dayjs('2023-12-06'),
};

export const sampleWithNewData: NewEmployee = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
