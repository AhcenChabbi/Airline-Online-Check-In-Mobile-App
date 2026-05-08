import express from "express";
import "dotenv/config";
import { OK } from "./constants/http";
import cors from "cors";
import cookieParser from "cookie-parser";
import morgan from "morgan";
import rateLimiter from "express-rate-limit";
import errHandler from "./middleware/errHandler";
import authRoutes from "./routes/auth.routes";
import usersRoutes from "./routes/users.routes";

const app = express();

app.use(express.json());

app.use(cors());

app.use(morgan("dev"));

app.use(cookieParser());

app.use(express.urlencoded({ extended: true }));

app.get("/health", async (req, res) => {
  res.status(OK).json({ message: "Server is healthy" });
});

app.use("/api/auth", authRoutes);
app.use("/api/users", usersRoutes);

app.use(errHandler);

export default app;
