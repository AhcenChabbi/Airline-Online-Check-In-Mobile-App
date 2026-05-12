import express from "express";
import "dotenv/config";
import { OK } from "./constants/http";
import cors from "cors";
import cookieParser from "cookie-parser";
import morgan from "morgan";
import errHandler from "./middleware/errHandler";
import authRoutes from "./routes/auth.routes";
import usersRoutes from "./routes/users.routes";
import bookingRoutes from "./routes/booking.routes";
import swaggerUi from "swagger-ui-express";
import { swaggerSpec } from "./docs/swagger";
import { NODE_ENV } from "./config/env";
import { requireAuth } from "./middleware/auth";
import checkinRoutes from "./routes/checkin.routes";
import { apiLimiter } from "./middleware/rateLimiter";
import { bullBoardServerAdapter } from "./config/bullBoard";

const app = express();

app.use(express.json());
app.use(cors());

app.use(morgan("dev"));

app.use(cookieParser());

app.use(express.urlencoded({ extended: true }));

app.use("/api", apiLimiter);
app.use("/api/auth", authRoutes);
app.use("/api/users", usersRoutes);
app.use("/api/bookings", requireAuth, bookingRoutes);
app.use("/api/checkin", checkinRoutes);

if (NODE_ENV === "development") {
  app.use("/admin/queues", bullBoardServerAdapter.getRouter());
  app.use("/api/docs", swaggerUi.serve, swaggerUi.setup(swaggerSpec));
  app.use("/api/docs.json", (req, res) => {
    res.setHeader("Content-Type", "application/json");
    res.send(swaggerSpec);
  });
}

app.get("/health", async (req, res) => {
  res.status(OK).json({ message: "Server is healthy" });
});

app.use(errHandler);

export default app;
