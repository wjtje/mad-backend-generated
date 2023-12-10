import dayjs from 'dayjs/esm';

import { IInspection, NewInspection } from './inspection.model';

export const sampleWithRequiredData: IInspection = {
  id: 8784,
};

export const sampleWithPartialData: IInspection = {
  id: 4710,
  code: 'validate recognize below',
  odometer: 19166,
  result: 'ew perfectly',
  photo: '../fake-data/blob/hipster.png',
  photoContentType: 'unknown',
  completed: dayjs('2023-12-07T06:33'),
};

export const sampleWithFullData: IInspection = {
  id: 13032,
  code: 'aw incandescence bah',
  odometer: 9313,
  result: 'yahoo although',
  photo: '../fake-data/blob/hipster.png',
  photoContentType: 'unknown',
  completed: dayjs('2023-12-06T12:24'),
};

export const sampleWithNewData: NewInspection = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
