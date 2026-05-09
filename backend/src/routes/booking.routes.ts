import { Router } from "express";
import { lookupBookingController } from "../controllers/booking.controller";

const router = Router();

router.post("/lookup", lookupBookingController);

export default router;
