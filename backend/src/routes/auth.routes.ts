import { Router } from "express";
import {
  loginWithGoogleController,
  loginWithEmailController,
  refreshTokenController,
  registerWithEmailController,
} from "../controllers/auth.controller";
import { authLimiter } from "../middleware/rateLimiter";

const router = Router();

router.use(authLimiter);

router.post("/register", registerWithEmailController);
router.post("/login", loginWithEmailController);
router.post("/google", loginWithGoogleController);
router.post("/refresh", refreshTokenController);

export default router;
