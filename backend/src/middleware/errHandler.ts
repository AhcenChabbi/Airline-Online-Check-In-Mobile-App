import { ErrorRequestHandler, Response } from "express";
import { ZodError } from "zod";
import { BAD_REQUEST, INTERNAL_SERVER_ERROR } from "../constants/http";
import AppError from "../utils/AppError";
import { NODE_ENV } from "../config/env";

const handleZodError = (res: Response, error: ZodError) => {
  const errors = error.issues.map((issue) => ({
    field: issue.path.join("."),
    message: issue.message,
  }));
  return res.status(BAD_REQUEST).json({ message: error.message, errors });
};

const handleAppError = (res: Response, error: AppError) => {
  return res.status(error.httpStatusCode).json({
    message: error.message,
  });
};

const errHandler: ErrorRequestHandler = (err, req, res, next) => {
  if (NODE_ENV === "development") {
    console.log(`PATH ${req.path}`, err);
  }

  if (err instanceof ZodError) {
    return handleZodError(res, err);
  }

  if (err instanceof AppError) {
    return handleAppError(res, err);
  }

  return res.status(INTERNAL_SERVER_ERROR).json({
    message: "internal server error",
  });
};

export default errHandler;
