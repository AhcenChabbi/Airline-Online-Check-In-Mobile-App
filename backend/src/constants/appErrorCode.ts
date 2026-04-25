const enum AppErrorCode {
  InvalidAccessToken = "INVALID_ACCESS_TOKEN",
  InvalidRefreshToken = "INVALID_REFRESH_TOKEN",
  UserNotFound = "USER_NOT_FOUND",
  EmailAlreadyExists = "EMAIL_ALREADY_EXISTS",
  InvalidCredentials = "INVALID_CREDENTIALS",
  Unauthorized = "UNAUTHORIZED",
  Forbidden = "FORBIDDEN",
  BadRequest = "BAD_REQUEST",
  InternalServerError = "INTERNAL_SERVER_ERROR",
}
export default AppErrorCode;
