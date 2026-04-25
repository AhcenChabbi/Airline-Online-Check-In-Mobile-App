import { NextFunction, Request, RequestHandler, Response } from "express";
const catchErrors =
  (controller: RequestHandler): RequestHandler =>
  (req: Request, res: Response, next: NextFunction) => {
    Promise.resolve(controller(req, res, next)).catch(next);
  };
export default catchErrors;
