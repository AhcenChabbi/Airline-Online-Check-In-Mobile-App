import { HttpStatusCode } from "../constants/http";

export default class AppError extends Error {
  constructor(
    public message: string,
    public httpStatusCode: HttpStatusCode,
  ) {
    super(message);
  }
}
