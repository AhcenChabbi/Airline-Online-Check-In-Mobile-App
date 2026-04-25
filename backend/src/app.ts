import express from "express";
import "dotenv/config";
import { OK } from "./constants/http";
import cors from "cors";
import cookieParser from "cookie-parser";
import morgan from "morgan";
import rateLimiter from "express-rate-limit";
import errHandler from "./middleware/errHandler";

const app = express();

const limiter = rateLimiter({
  windowMs: 5 * 60 * 1000, // 5 minutes
  max: 100, // limit each IP to 100 requests per windowMs
  message: "Too many requests from this IP, please try again after 15 minutes",
});

app.use(express.json());

app.use(cors());

app.use(limiter);

app.use(morgan("dev"));

app.use(cookieParser());

app.use(express.urlencoded({ extended: true }));

app.get("/health", async (req, res) => {
  res.status(OK).json({ message: "Server is healthy" });
});

app.use(errHandler);

export default app;
