import { requireAuth } from './../middleware/auth';
import { Router } from "express";
import * as CheckInController from "../controllers/checkin.controller.js";

const router = Router();

// All check-in routes require authentication
router.use(requireAuth);

router.post("/initiate", CheckInController.initiateCheckIn);
router.post("/:checkinId/passport", CheckInController.submitPassport);
router.post("/:checkinId/details/confirm", CheckInController.confirmDetails);
router.get("/:checkinId/seats", CheckInController.getSeatMap);
router.post("/:checkinId/seats/select", CheckInController.selectSeat);
router.post("/:checkinId/baggage", CheckInController.declareBaggage);
router.post("/:checkinId/special-requests", CheckInController.submitSpecialRequests);
router.post("/:checkinId/confirm", CheckInController.confirmCheckIn);
router.get("/:checkinId/boarding-pass", CheckInController.getBoardingPass);
router.get("/:checkinId/boarding-pass/pdf", CheckInController.downloadBoardingPassPdf);

export default router;