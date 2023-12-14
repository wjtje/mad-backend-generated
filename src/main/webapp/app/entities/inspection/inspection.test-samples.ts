import dayjs from 'dayjs/esm';

import { IInspection, NewInspection } from './inspection.model';

export const sampleWithRequiredData: IInspection = {
  id: 9103,
};

export const sampleWithPartialData: IInspection = {
  id: 30060,
  code: 'hence',
  odometer: 6819,
  completed: dayjs('2023-12-06T17:22'),
};

export const sampleWithFullData: IInspection = {
  id: 32067,
  code: 'reminisce',
  odometer: 17557,
  result: 'busily',
  photo: '../fake-data/blob/hipster.png',
  photoContentType: 'unknown',
  completed: dayjs('2023-12-06T23:59'),
};

export const sampleWithNewData: NewInspection = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
