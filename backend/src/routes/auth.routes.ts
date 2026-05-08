import { Router } from "express";
import {
  loginWithGoogleController,
  loginWithEmailController,
  refreshTokenController,
  registerWithEmailController,
} from "../controllers/auth.controller";
import {
  authRateLimiter,
  bruteForceProtection,
} from "../middleware/authProtection";

const router = Router();

router.use(authRateLimiter);

router.post("/register", registerWithEmailController);
router.post("/login", bruteForceProtection, loginWithEmailController);
router.post("/google", bruteForceProtection, loginWithGoogleController);
router.post("/refresh", bruteForceProtection, refreshTokenController);

export default router;
