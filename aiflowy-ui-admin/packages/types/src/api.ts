export interface RequestResult<T> {
  data: T;
  errorCode: number;
  message: string;
}
